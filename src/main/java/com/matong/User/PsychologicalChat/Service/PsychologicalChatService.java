package com.matong.User.PsychologicalChat.Service;

import com.matong.User.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.User.PsychologicalChat.VO.StructOutPutResponse;
import reactor.core.publisher.Flux;

public interface PsychologicalChatService {
    /**
     * 创建会话
     * @param userId
     * @param createDTO
     */
    StructOutPutResponse.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) ;
    /**
     * 流式对话
     * @param sessionId
     * @param userMessage
     * @return
     */
    Flux<String> streamPsychologicalChat(String sessionId , String userMessage);
}
