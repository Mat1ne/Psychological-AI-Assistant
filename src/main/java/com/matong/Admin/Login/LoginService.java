package com.matong.Admin.Login;

import com.matong.Admin.Login.DTO.UserLoginCommandDTO;
import com.matong.Admin.Login.VO.UserLoginResponseDTO;


public interface LoginService {
    /**
     * 登入接口
     * @param userLoginCommandDTO
     * @return
     */
    UserLoginResponseDTO login(UserLoginCommandDTO userLoginCommandDTO);
}
