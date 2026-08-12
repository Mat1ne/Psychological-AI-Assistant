package com.matong.Admin.Util;

import com.matong.Admin.Common.ResultCode;
import com.matong.Admin.Config.SecurityConfig;
import com.matong.Admin.EnumClass.UserStatus;
import com.matong.Admin.Login.Entity.User;
import com.matong.Admin.Login.LoginService;
import com.matong.Admin.Login.VO.UserLoginResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri =  request.getRequestURI();
        return SecurityConfig.isPublicUrl(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        //从请求头中提取token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if(StringUtils.hasText(token)) {
             //校验token并获取用户信息
             JwtTokenUtil.TokenVerificationResult validationResult = JwtTokenUtil.validateToken(token);
             if(validationResult != null && validationResult.isValid()){
                 //查询用户信息
                 UserLoginResponseDTO.UserDetailResponseDTO user = loginService.getByUserId(validationResult.getUserId());
                 if(user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())) {
                     //创建Spring Security认证对象
                     List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                             new SimpleGrantedAuthority("ROLE_" + validationResult.getRoleType())
                     );
                     //创建UsernamePasswordAuthenticationToken对象
                     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                             validationResult.getUsername(),
                             null,
                             authorities
                     );
                     //设置认证信息到Spring Security上下文
                     SecurityContextHolder.getContext().setAuthentication(authentication);
                     //将token存储到请求属性中
                     request.setAttribute("jwtToken", token);

                 }else{
                     clearSecurityContext();
                     ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                 }
             }else{
                 clearSecurityContext();
                 ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
             }
        }else {
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }
        //继续过滤器链
        chain.doFilter(request, response);
    }
    //清理Spring Security上下文
    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
