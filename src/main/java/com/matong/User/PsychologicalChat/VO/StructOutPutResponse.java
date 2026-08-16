package com.matong.User.PsychologicalChat.VO;

public class StructOutPutResponse {
    public record StreamChatSession(
            String sessionId,
            Long userHash,
            String initialMessage,
            Long startTime,
            Long expiryTime,
            Integer messageCount,
            String status
    ) {}
}
