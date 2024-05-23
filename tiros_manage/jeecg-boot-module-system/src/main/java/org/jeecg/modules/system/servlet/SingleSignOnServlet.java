package org.jeecg.modules.system.servlet;

import bingo.sso.client.web.AbstractSingleSignOnServlet;
import bingo.sso.client.web.Authentication;
import bingo.sso.client.web.SingleSignOnConfigHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jak
 * @date 2022/10/9 18:57
 * @version: V1.0
 */

@Slf4j
@WebServlet(name = "ssoclient", urlPatterns = "/ssoclient/*", initParams = {
        @WebInitParam(name = "ssoBaseEndpoint", value = "http://120.197.17.151:40002/sso"),
        @WebInitParam(name = "clientId", value = "a9efd36c-37fb-11ed-aba9-0050569770b5"),
        @WebInitParam(name = "clientSecret", value = "a9efd3b8-37fb-11ed-aba9-0050569770b5", description = "detail-info")
})
public class SingleSignOnServlet extends AbstractSingleSignOnServlet {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private RedisUtil redisUtil;

    public static final String KEY_SESSION_AUTH = SingleSignOnServlet.class.getName() + "$AUTHENTICATION";
    public static final String KEY_SESSION_ACCOUNT = SingleSignOnServlet.class.getName() + "$ACCOUNT_NAME";
    public static final String KEY_SESSION_TOKEN = SingleSignOnServlet.class.getName() + "$SSO_TOKEN";

  private static final String IS_SSO_LOGIN_CHECK_KEY = SingleSignOnServlet.class.getName() + "$IS_SSO_LOGIN_CHECK";

    /*  public static Authentication getAuthentication(HttpServletRequest req) {
        return (Authentication) req.getSession().getAttribute(KEY_SESSION_AUTH);
    }

    public static boolean isSsoOLoginCheck(HttpServletRequest req) {
        return req.getSession().getAttribute(IS_SSO_LOGIN_CHECK_KEY) != null;
    }*/
    @PostConstruct
    public void init_post() {
        Map<String, String> params = new HashMap<>();
        params.put("ssoBaseEndpoint", "http://10.97.10.5:8080/sso");
        params.put("clientId", "a9efd36c-37fb-11ed-aba9-0050569770b5");
        params.put("clientSecret", "a9efd3b8-37fb-11ed-aba9-0050569770b5");
        this.setInitParam(params);
    }


    /**
     * 登录成功会回调方法实现
     * @param req
     * @param response
     * @param auth 授权凭证
     */
    protected void onSuccessSignIn(HttpServletRequest req, HttpServletResponse response, Authentication auth)
            throws Exception {

        // SSO登陆成功，保存登陆成功信息到Session中
        /*req.getSession().setAttribute(KEY_SESSION_AUTH, auth);
        req.getSession().setAttribute(KEY_SESSION_ACCOUNT, auth.getIdentity());
        req.getSession().setAttribute(KEY_SESSION_TOKEN, auth.getUserProperties().get("ex.token"));*/

        log.info("====================单点登录成功：" + auth.getIdentity());
        //判断有没有这个人
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
        query.eq(SysUser::getUsername, auth.getIdentity());
        List<SysUser> users = sysUserService.list(query);
        if (users != null && users.size() > 0) {
            SysUser user = users.get(0);
            // 生成token
            String token = JwtUtil.sign(user.getId(), user.getUsername(), user.getPassword());
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);

            response.sendRedirect("http://localhost:3000/sso/login?token=" + token);
        } else {
            response.getWriter().print("登录失败,架大修系统不存在该用户！");
        }
    }

    @Override
    protected void onSetupNeeded(HttpServletRequest req, HttpServletResponse response, String returnUrl)
            throws Exception {
        req.getSession().setAttribute(IS_SSO_LOGIN_CHECK_KEY, true);
        response.sendRedirect(returnUrl);
    }
}
