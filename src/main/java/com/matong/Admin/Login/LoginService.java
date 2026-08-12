package com.matong.Admin.Login;

import com.matong.Admin.Login.DTO.UserLoginCommandDTO;
import com.matong.Admin.Login.DTO.UserRegisterCommandDTO;
import com.matong.Admin.Login.VO.UserLoginResponseDTO;
import jakarta.validation.Valid;


public interface LoginService {
    /**
     * 登入接口
     * @param userLoginCommandDTO
     * @return
     */
    UserLoginResponseDTO login(UserLoginCommandDTO userLoginCommandDTO);

    /**
     * 注册接口
     * @param userRegisterCommandDTO
     * @return
     */
    UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO userRegisterCommandDTO);

    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    UserLoginResponseDTO.UserDetailResponseDTO getByUserId(Long userId);
}
