package com.matong.User.PsychologicalChat.ServiceImpl;

import com.matong.Admin.Config.PromptManage;
import com.matong.User.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.User.PsychologicalChat.Entity.ConsultationSession;
import com.matong.User.PsychologicalChat.Service.ConsultationMessageService;
import com.matong.User.PsychologicalChat.Service.ConsultationSessionService;
import com.matong.User.PsychologicalChat.Service.PsychologicalChatService;
import com.matong.User.PsychologicalChat.VO.ConsultationMessageResponseDTO;
import com.matong.User.PsychologicalChat.VO.StructOutPutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PsychologicalChatServiceImpl implements PsychologicalChatService {
    @Qualifier("open-ai")
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ConsultationSessionService consultationSessionService;
    private final ConsultationMessageService consultationMessageService;
    @Override
    public StructOutPutResponse.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //创建数据库会话记录
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);
        //将初始用户消息保存到message中
        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(), null);
        //创建会话信息
        String sessionId = "session_" + consultationSession.getId();
        return new StructOutPutResponse.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L,//24小时
                1,
                "ACTIVE"
        );
    }
    public Flux<String> streamPsychologicalChat(String sessionId , String userMessage) {
        return Flux.create(sink -> {
            //Flux.next("数据");发布数据
            //Flux.error(new RuntimeException("会话ID格式错误"));发布错误
            //Flux.complete();发布完成
            Long dbSessionId = extractSessionId(sessionId);
            if(dbSessionId == null) {
                sink.error(new RuntimeException("会话ID格式错误"));
            }
            boolean isInitialMessage = false;
            //检查是否是初始消息
            Integer messageCount = consultationMessageService.getMessageCountBySessionId(dbSessionId);
            if(messageCount == 1) {
                ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(dbSessionId);
                if(lastMessage != null && lastMessage.getSenderType() == 1 && userMessage.equals(lastMessage.getContent())) {
                    isInitialMessage = true;
                }
            }
            if(!isInitialMessage) {
                //保存用户信息到数据库
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }
            //进行流式对话，构建系统提示词
            //生成对话记忆管理
            StringBuilder fullResponse = new StringBuilder();
            String conversationId = "conversation_" + sessionId;
            List<Message> userMessages = new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));
            chatMemory.add(conversationId , userMessages);
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));
            chatClient.prompt(prompt)
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID , conversationId))
                    .stream()
                    .content()
                    .doOnNext(Fragment ->{
                        fullResponse.append(Fragment);
                        sink.next(Fragment);
                    })
                    .doOnComplete(()->{
                        String completeRes = fullResponse.toString();
                        consultationMessageService.saveAiMessage(dbSessionId, completeRes, "openai");
                        List<Message> aiMessages = new ArrayList<>();
                        aiMessages.add(new AssistantMessage(completeRes));
                        chatMemory.add(conversationId , aiMessages);
                        sink.complete();
                    })
                    .doOnError(error->{
                        sink.error(error);
                    })
                    .subscribe();//订阅并启动流

        });
    }

    //获取参数中的SessionId
    public Long extractSessionId(String sessionId){
        if(sessionId != null && sessionId.contains("session_")){
            String idStr = sessionId.substring("session_".length());
            return Long.parseLong(idStr);
        }
        return null;
    }
}
