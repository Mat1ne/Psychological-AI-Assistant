package com.matong.Admin.Login;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matong.Admin.Login.Entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper extends BaseMapper<User> {
}
