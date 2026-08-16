package com.matong.User.PsychologicalChat.Service;

import com.matong.User.PsychologicalChat.Entity.ConsultationMessage;
import com.matong.User.PsychologicalChat.VO.ConsultationMessageResponseDTO;

public interface ConsultationMessageService {
    /**
     * 保存用户会话记录
     * @param sessionId
     * @param content
     * @param emotion_tag
     */
    ConsultationMessage saveUserMessage(Long sessionId , String content , String emotion_tag );

    /**
    * 获取用户会话记录数量
    * @param sessionId
     * @return
     */
    Integer getMessageCountBySessionId(Long sessionId);

    /**
     * 获取用户会话记录中的最后一条消息
     * @param dbSessionId
     * @return
     */
    ConsultationMessageResponseDTO getLastMessageBySessionId(Long dbSessionId);

    /**
     * 保存AI助手会话记录
     * @param dbSessionId
     * @param completeRes
     * @param openai
     */
    ConsultationMessage saveAiMessage(Long dbSessionId, String completeRes, String openai);
}
