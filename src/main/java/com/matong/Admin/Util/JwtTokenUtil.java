package com.matong.Admin.Util;

import ch.qos.logback.core.util.StringUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.matong.Admin.Config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static final String ISSUER = "Psychological_AI_Assistant";
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JwtTokenUtil.applicationContext = applicationContext;
    }
    private static JwtConfig getJwtConfig() {
        return JwtTokenUtil.applicationContext.getBean(JwtConfig.class);
    }

    public static String generateToken(Long userId , String userName , Integer roleType) {
        try {
            JwtConfig jwtConfig = getJwtConfig();
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            Date expiration = new Date(System.currentTimeMillis() +  jwtConfig.getExpiration());

            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim("userId" ,  userId)
                    .withClaim("userName", userName)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expiration)
                    .withIssuedAt(new Date())
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成JWT失败", e);
        }
    }
    //提取token
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        if(StringUtils.hasText(request.getHeader("token"))){
            return request.getHeader("token");
        }
        return null;
    }
    //获取当前token
    public static String getCurrentToken(){
        //拿到当前线程的http请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            String token = (String)request.getAttribute("jwtToken");
            if(token != null){
                return token;
            }
            //备用方案：从请求头获取
            String headerToken = extractTokenFromRequest(request);
            return headerToken;
        }
        return null;
    }
    //验证token
    public static TokenVerificationResult validateToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        String userName = jwt.getClaim("userName").asString();
        //角色类型
        Integer roleType = null;
        try {
            roleType = jwt.getClaim("roleType").asInt();
        }catch (Exception e){
            String roleTypeStr = jwt.getClaim("roleType").asString();
            if(StringUtils.hasText(roleTypeStr)){
                roleType = Integer.valueOf(roleTypeStr);
            }
        }
        if(userId != null && StringUtils.hasText(userName) && roleType != null){
            return new TokenVerificationResult(userId, true, userName, roleType);
        }
        return null;
    }
    public static DecodedJWT verifyToken(String token){
        if(!StringUtils.hasText(token)){
            throw new JWTVerificationException("token不能为空");
        }
        //token解码
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }
    //token验证结果封装
    @Getter
    public static class TokenVerificationResult {
        private final Long userId;
        private final boolean valid;
        private final String username;
        private final Integer roleType;

        public TokenVerificationResult(Long userId, Boolean valid, String username, Integer roleType) {
            this.userId = userId;
            this.valid = valid;
            this.username = username;
            this.roleType = roleType;
        }
    }
}
