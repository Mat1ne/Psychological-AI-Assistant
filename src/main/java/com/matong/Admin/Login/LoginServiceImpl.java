package com.matong.Admin.Login;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matong.Admin.Exception.BusinessException;
import com.matong.Admin.Login.Convert.UserConvert;
import com.matong.Admin.Login.DTO.UserLoginCommandDTO;
import com.matong.Admin.Login.Entity.User;
import com.matong.Admin.Login.VO.UserLoginResponseDTO;
import com.matong.Admin.Util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
//RequiredArgsConstructor注解会自动注入LoginMapper和BCryptPasswordEncoder的Bean,用final修饰
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    //密码加密工具
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final LoginMapper loginMapper;


    @Override
    public UserLoginResponseDTO login(UserLoginCommandDTO userLoginCommandDTO) {

        //自动写入Query语句，根据用户名或邮箱查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userLoginCommandDTO.getUsername())
                .or()
                .eq(User::getEmail, userLoginCommandDTO.getUsername());
        User user = loginMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        //trim()方法会移除字符串首尾的空格
        String inputPassword = userLoginCommandDTO.getPassword().trim();
        if(!bCryptPasswordEncoder.matches(inputPassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if(!user.isActive()) {
            throw new BusinessException("用户已被禁用,请联系管理员");
        }
        String token = JwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        UserLoginResponseDTO.UserDetailResponseDTO info = UserConvert.entityToDetailResponse(user);
        return UserConvert.entityToLoginResponse(token, info);
    }
}
