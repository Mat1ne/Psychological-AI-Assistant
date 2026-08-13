package com.matong.Admin.PsychologicalChat.Service;

import com.matong.Admin.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.Admin.PsychologicalChat.Entity.ConsultationSession;

public interface ConsultationSessionService {
    /**
     * 创建会话
     * @param userId
     * @param createDTO
     */
    ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO);
}
