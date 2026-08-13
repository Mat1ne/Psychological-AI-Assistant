package com.matong.Admin.PsychologicalChat;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.matong.Admin.Common.Result;
import com.matong.Admin.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.Admin.PsychologicalChat.Service.PsychologicalChatService;
import com.matong.Admin.PsychologicalChat.VO.StructOutPutResponse;
import com.matong.Admin.Util.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psychological-chat")
@RequiredArgsConstructor
public class PsychologicalChatController {
    private final PsychologicalChatService psychologicalChatService;

    @PostMapping("/session/start")
    public Result<StructOutPutResponse.StreamChatSession> startSession(@Valid @RequestBody ConsultationSessionCreateDTO createDTO){
        //获取当前用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        //创建会话
        StructOutPutResponse.StreamChatSession session = psychologicalChatService.startSession(userId, createDTO);
        return Result.success(session);
    }
}
