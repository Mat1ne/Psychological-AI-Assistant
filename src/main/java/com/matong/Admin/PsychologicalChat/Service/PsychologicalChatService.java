package com.matong.Admin.PsychologicalChat.Service;

import com.matong.Admin.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.Admin.PsychologicalChat.VO.StructOutPutResponse;
import jakarta.validation.Valid;

import java.sql.Struct;

public interface PsychologicalChatService {
    /**
     * 创建会话
     * @param userId
     * @param createDTO
     */
    StructOutPutResponse.StreamChatSession startSession(Long userId, @Valid ConsultationSessionCreateDTO createDTO) ;
}
