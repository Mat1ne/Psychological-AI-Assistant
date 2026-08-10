package com.matong.Admin.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
//将JwtConfig类标记为组件，将其添加到Spring容器中，作为一个Bean
@Component
@Data
//将application.properties中的jwt配置项注入到JwtConfig类中
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private long expiration;
    private long refreshExpiration;
    private String header;
    private String tokenPrefix;
}
