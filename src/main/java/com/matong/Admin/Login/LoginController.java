package com.matong.Admin.Login;

import com.matong.Admin.Common.Result;
import com.matong.Admin.Login.DTO.UserLoginCommandDTO;
import com.matong.Admin.Login.VO.UserLoginResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    //@Valid 校验RequestBody中的数据是否符合DTO的注解约束
    //返回的是ResponseDTO，包含token、roleType、userInfo等信息，区别于CommandDTO中的username、password
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO userLoginCommandDTO) {
        UserLoginResponseDTO result = loginService.login(userLoginCommandDTO);
        return Result.success(result);
    }
}
