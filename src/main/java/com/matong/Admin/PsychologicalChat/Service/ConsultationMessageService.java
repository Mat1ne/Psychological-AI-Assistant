package com.matong.Admin.PsychologicalChat.Service;

import com.matong.Admin.PsychologicalChat.Entity.ConsultationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface ConsultationMessageService {
    /**
     * 保存用户会话记录
     * @param sessionId
     * @param content
     * @param emotion_tag
     */
    ConsultationMessage saveUserMessage(Long sessionId , String content , String emotion_tag );
}
