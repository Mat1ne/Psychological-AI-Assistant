package com.matong.User.PsychologicalChat;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.matong.Admin.Common.Result;
import com.matong.Admin.Common.ResultCode;
import com.matong.User.PsychologicalChat.DTO.ConsultationSessionCreateDTO;
import com.matong.User.PsychologicalChat.DTO.ConsultationStreamDTO;
import com.matong.User.PsychologicalChat.Service.PsychologicalChatService;
import com.matong.User.PsychologicalChat.VO.StructOutPutResponse;
import com.matong.Admin.Util.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

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

    //produces = MediaType.TEXT_EVENT_STREAM_VALUE 表示返回的是文本事件流，用于流式传输
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO) {
        // 获取当前用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        if (userId == null) {
            //SSE适用于流式传输，这里返回一个错误事件
            //WebSocket 适用于长连接
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data(JSONUtil.toJsonStr(Result.error(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMsg(), "用户未登录")))
                    .build());
        }

        // 开始流式对话
        return psychologicalChatService.streamPsychologicalChat(streamDTO.getSessionId(), streamDTO.getUserMessage())
                .map(Fragment -> {
                    return ServerSentEvent.<String>builder()
                            .event("message")
                            .data(JSONUtil.toJsonStr(Result.success(Map.of("content", Fragment, "type", "normal"))))
                            .build();
                })
                //concatWith 是Reactor框架中的操作符，用于在响应式流的末尾追加另一个流
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .event("done")
                        .data("{}")
                        .build()
                ))
                .delayElements(Duration.ofMillis(50)); // 添加延迟确保流式数据的体验
    }
}
