package com.matong.Admin.Config;

import cn.hutool.core.text.AntPathMatcher;
import com.matong.Admin.Login.LoginService;
import com.matong.Admin.Util.JwtAuthticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Configuration会在IOC容器中拿取单例Bean，不会重新创建
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginService loginService;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_URLS = {
            "/",
            "/error",
            "/api/login",
            "/api/user/login",
            "/api/user/add"
    };
    public static Boolean isPublicUrl(HttpServletRequest request) {
        for (String publicUri : PUBLIC_URLS) {
            if(antPathMatcher.match(publicUri, request.getRequestURI())){
                return true;
            }
        }
        return false;
    }
    @Bean
    public JwtAuthticationFilter jwtAuthticationFilter() {
        return new JwtAuthticationFilter(loginService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用会话管理
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        //无需权限即可访问的URL
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        // 其他请求都需要认证
                        .anyRequest().authenticated()
                )
                //添加jwt过滤器
                .addFilterBefore(jwtAuthticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
