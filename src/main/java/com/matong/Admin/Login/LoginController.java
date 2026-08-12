package com.matong.Admin.Login;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.matong.Admin.Common.Result;
import com.matong.Admin.Login.DTO.UserLoginCommandDTO;
import com.matong.Admin.Login.DTO.UserRegisterCommandDTO;
import com.matong.Admin.Login.VO.UserLoginResponseDTO;
import com.matong.Admin.Util.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    //@Valid 校验RequestBody中的数据是否符合DTO的注解约束
    //返回的是ResponseDTO，包含token、roleType、userInfo等信息，区别于CommandDTO中的username、password
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO userLoginCommandDTO) {
        UserLoginResponseDTO result = loginService.login(userLoginCommandDTO);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO userRegisterCommandDTO) {
        UserLoginResponseDTO.UserDetailResponseDTO userinfo = loginService.register(userRegisterCommandDTO);
        return Result.success(userinfo);
    }
    @GetMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> getCurrentInfo() {
        //如何从token中获取当前用户信息
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        UserLoginResponseDTO.UserDetailResponseDTO userinfo = loginService.getByUserId(userId);
        return Result.success(userinfo);
    }
}
