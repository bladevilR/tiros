package org.jeecg.modules.system.oauth2_sso;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jak
 * @date 2022/10/10 8:48
 * @version: V1.0
 */
@Component
@EnableConfigurationProperties(value = Oauth2SsoConfig.class)
public class Oauth2Client {
    @Resource
    Oauth2SsoConfig oauth2SsoConfig;

    public String getWebAddress() {
        return this.oauth2SsoConfig.getWebAddress();
    }

    /**
     * 获取单点登录的授权地址
     * @author Jak
     * @date 2022/10/10 9:37
     * @version V1.0
     */
    public String getAuthorizeUri() {
        //  https://xxxxxx/oauth/authorize?
        //  response_type=code&
        //  client_id=CLIENT_ID&
        //  redirect_uri=CALLBACK_URL&
        //  scope=read
        String url = StrUtil.format("{}?response_type=code&client_id={}&redirect_uri={}&logout_uri={}",
                this.oauth2SsoConfig.getProvider().getAuthorizationUri(),
                this.oauth2SsoConfig.getRegistration().getClientId(),
                this.oauth2SsoConfig.getRegistration().getRedirectUri(),
                this.oauth2SsoConfig.getRegistration().getLogoutNoticeUri()
                );
        return url;
    }

    public String getLogoutUri() {
        // /oauth2/logout?
        String url = StrUtil.format("{}?post_logout_redirect_uri={}"
                ,this.oauth2SsoConfig.getProvider().getLogoutUri()
                ,this.oauth2SsoConfig.getRegistration().getLogoutRedirectUri()
        );
        return url;
    }

    public String getAccessTokenUri(String code) {
        // https://b.com/oauth/token?
        // client_id=CLIENT_ID&
        // client_secret=CLIENT_SECRET&
        // grant_type=authorization_code&
        // code=AUTHORIZATION_CODE&
        // redirect_uri=CALLBACK_URL
        String url = StrUtil.format("{}?client_id={}&client_secret={}&grant_type={}&code={}"
                ,this.oauth2SsoConfig.getProvider().getTokenUri()
                ,this.oauth2SsoConfig.getRegistration().getClientId()
                ,this.oauth2SsoConfig.getRegistration().getClientSecret()
                ,this.oauth2SsoConfig.getRegistration().getGrantType()
                ,code
        );
        return url;
    }

    public String getUserInfoUri(String accessToken) {
        String url = StrUtil.format("{}?access_token={}",this.oauth2SsoConfig.getProvider().getUserInfoUri(), accessToken);
        return url;
    }
}
