package com.matong.User.PsychologicalChat.Service;

import com.matong.User.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.User.PsychologicalChat.Entity.ConsultationSession;

public interface ConsultationSessionService {
    /**
     * 创建会话
     * @param userId
     * @param createDTO
     */
    ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO);
}
