package org.jeecg.modules.system.oauth2_sso;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jak
 * @date 2022/10/10 8:49
 * @version: V1.0
 * oauth2:
 *   server: http://zhdg.gziic.cn:10007/sso
 *   provider:
*             authorization-uri: ${oauth2.server}/oauth2/authorize          # 授权url固定值
*             token-uri: ${oauth2.server}/oauth2/token                      # 获取令牌url，固定值
*             user-info-uri: ${oauth2.server}/oauth2/userinfo               # 获取用户信息url，固定值
*             userNameAttribute: username                                   # 用户信息中获取用户名的属性，固定值
*             jwk-set-uri: ${oauth2.server}/oauth2/token_keys               # 获取加密公钥url，固定值
 *         registration:
 *             client-id: clientId                                           # 客户端账号，默认值clientId，定制值需在sso中申请
 *             client-secret: clientSecret                                   # 客户端密钥，默认值clientSecret，定制值需在sso中申请
 *             authorization-grant-type: authorization_code                  # 固定值
 *             redirect-uri: '{baseUrl}/login/oauth2/code/{registrationId}'
 */
@Data
@ConfigurationProperties(prefix = "oauth2")
public class Oauth2SsoConfig {
    private String server;
    private String webAddress;
    private Oauth2Provider provider=new Oauth2Provider();
    private Oauth2Registration registration=new Oauth2Registration();

    @Data
    public class Oauth2Provider {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String userNameAttribute = "username";
        private String jwkSetUri;
        private String logoutUri;
    }

    @Data
    public class Oauth2Registration {
        private String clientId;
        private String clientSecret;
        private String grantType = "authorization_code";
        private String redirectUri;
        private String logoutNoticeUri;
        private String logoutRedirectUri;
    }
}
