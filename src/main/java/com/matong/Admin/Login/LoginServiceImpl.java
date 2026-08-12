package com.matong.Admin.Login;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matong.Admin.EnumClass.UserStatus;
import com.matong.Admin.EnumClass.UserType;
import com.matong.Admin.Exception.BusinessException;
import com.matong.Admin.Login.Convert.UserConvert;
import com.matong.Admin.Login.DTO.UserLoginCommandDTO;
import com.matong.Admin.Login.DTO.UserRegisterCommandDTO;
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
    //注册用户
    @Override
    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO userRegisterCommandDTO) {
        System.out.println(JSONUtil.parseObj(userRegisterCommandDTO));
        if(!userRegisterCommandDTO.getPassword().equals(userRegisterCommandDTO.getConfirmPassword())) {
            throw new BusinessException("密码与确认密码不一致");
        }
        LambdaQueryWrapper<User> EmailQueryWrapper = new LambdaQueryWrapper<>();
        EmailQueryWrapper.eq(User::getUsername, userRegisterCommandDTO.getUsername())
                .or()
                .eq(User::getEmail, userRegisterCommandDTO.getUsername());
        if (loginMapper.selectOne(EmailQueryWrapper) != null) {
            throw new BusinessException("用户邮箱已存在");
        }
        LambdaQueryWrapper<User> UsernameQueryWrapper = new LambdaQueryWrapper<>();
        UsernameQueryWrapper.eq(User::getUsername, userRegisterCommandDTO.getUsername());
        if (loginMapper.selectOne(UsernameQueryWrapper) != null) {
            throw new BusinessException("用户名已存在");
        }
        if(!UserType.isValidCode(userRegisterCommandDTO.getUserType())) {
            throw new BusinessException("用户类型无效");
        }
        String password = userRegisterCommandDTO.getPassword().trim();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user = UserConvert.registerCommandToEntity(userRegisterCommandDTO, encodedPassword);
        loginMapper.insert(user);

        return UserConvert.entityToDetailResponse(user);
    }

    @Override
    public UserLoginResponseDTO.UserDetailResponseDTO getByUserId(Long userId) {
        User user = loginMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }
}
