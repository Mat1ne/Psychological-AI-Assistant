package com.matong.Admin.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.matong.Admin.Config.JwtConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
}
