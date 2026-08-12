package com.matong.Admin.Util;

import cn.hutool.json.JSONUtil;
import com.matong.Admin.Common.Result;
import com.matong.Admin.Common.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    //过滤器中的异常反应
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        int status = switch (resultCode) {
            case UNAUTHORIZED , ACCESS_UNAUTHORIZED , TOKEN_INVALID ,  TOKEN_BLOCKED -> HttpStatus.UNAUTHORIZED.value();
            case TOKEN_ACCESS_FORBIDDEN ->  HttpStatus.FORBIDDEN.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //将异常信息写入响应体
        //PrintWriter 是一个字符流，用于将字符串写入到响应体中，向通道写入内容前端就能收到
        try (PrintWriter writer = response.getWriter()){
            String jsonResponse = JSONUtil.toJsonStr(Result.error(resultCode.getCode(), resultCode.getMsg(), null));
            writer.print(jsonResponse);
            writer.flush(); // 确保将响应内容写入到输出流
        }catch (IOException e) {
            System.out.println("写入响应失败：" + e.getMessage());
        }
    }
}
