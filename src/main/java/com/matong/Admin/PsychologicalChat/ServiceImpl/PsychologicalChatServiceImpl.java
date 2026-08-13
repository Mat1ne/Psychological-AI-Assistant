package com.matong.Admin.PsychologicalChat.ServiceImpl;

import com.matong.Admin.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.Admin.PsychologicalChat.Entity.ConsultationSession;
import com.matong.Admin.PsychologicalChat.Service.ConsultationMessageService;
import com.matong.Admin.PsychologicalChat.Service.ConsultationSessionService;
import com.matong.Admin.PsychologicalChat.Service.PsychologicalChatService;
import com.matong.Admin.PsychologicalChat.VO.StructOutPutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PsychologicalChatServiceImpl implements PsychologicalChatService {
    private final ConsultationSessionService consultationSessionService;
    private final ConsultationMessageService consultationMessageService;
    @Override
    public StructOutPutResponse.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //创建数据库会话记录
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);
        //将初始用户消息保存到message中
        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(), null);
        //创建会话信息
        String sessionId = "session - " + consultationSession.getId();
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
}
