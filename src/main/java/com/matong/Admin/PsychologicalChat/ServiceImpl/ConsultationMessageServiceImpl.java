package com.matong.Admin.PsychologicalChat.ServiceImpl;

import com.matong.Admin.PsychologicalChat.Entity.ConsultationMessage;
import com.matong.Admin.PsychologicalChat.Mapper.ConsultationMessageMapper;
import com.matong.Admin.PsychologicalChat.Service.ConsultationMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationMessageServiceImpl implements ConsultationMessageService {
    private final ConsultationMessageMapper consultationMessageMapper;

    @Override
    public ConsultationMessage saveUserMessage(Long sessionId, String content, String emotion_tag) {
        log.info("保存用户会话记录: sessionId={}, content={}, emotion_tag={}", sessionId, content, emotion_tag);
        //构建用户消息实体
        ConsultationMessage userMessage = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .emotionTag(emotion_tag)
                .aiModel("OpenAI")
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(userMessage);
        return userMessage;
    }
}
