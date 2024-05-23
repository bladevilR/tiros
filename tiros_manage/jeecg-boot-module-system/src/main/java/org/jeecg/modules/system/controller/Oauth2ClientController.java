package org.jeecg.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.RestUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.oauth2_sso.Oauth2Client;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Jak
 * @date 2022/10/10 9:52
 * @version: V1.0
 */
@Slf4j
@Api("Oauth2单点登录接口")
@Controller
@RequestMapping("/oauth2")
public class Oauth2ClientController {
    @Resource
    private Oauth2Client oauth2Client;

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("oauth2单点登录")
    @RequestMapping("/login")
    public void ssoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 判断当前是否已经登录
        boolean isLogin = false;
        String token = request.getParameter("token");
        if (StrUtil.isNotEmpty(token)) {
            String userName = JwtUtil.getUsername(token);
            isLogin = JwtUtil.verify(token, userName, null);
        }

        if (isLogin) {
            // 已经登录了， 跳转到首页
            String url = this.oauth2Client.getWebAddress() + "/index?token=" + token;
            response.sendRedirect(url);
        } else {
            // 没有登录，跳转到sso登录地址
            response.sendRedirect(oauth2Client.getAuthorizeUri());
        }

    }

    @RequestMapping("/login/callback")
    public void authorizeCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 去获取token
        String code = request.getParameter("code");
        log.info("==================>>> 用户登录成功：{}" , code);
        response.setContentType("text/html; charset=UTF-8");

        try {
            JSONObject jsonObject = RestUtil.post(this.oauth2Client.getAccessTokenUri(code));
            String accessToken= jsonObject.getString("access_token");
            log.info("==================>>> 获取token成功：{}",jsonObject.toJSONString());

            JSONObject userJson = RestUtil.get(this.oauth2Client.getUserInfoUri(accessToken));

            String username = userJson.getString("username");

            //判断有没有这个人
            LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
            query.eq(SysUser::getUsername, username);
            List<SysUser> users = sysUserService.list(query);
            if (users != null && users.size() > 0) {
                SysUser user = users.get(0);
                // 生成token
                String token = JwtUtil.sign(user.getId(), user.getUsername(), user.getPassword());
                redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);

                String url = this.oauth2Client.getWebAddress() + "/sso/login?token=" + token;
                response.sendRedirect(url);
            } else {
                response.getWriter().print("登录失败,架大修系统不存在该用户！");
            }

        } catch (Exception ex) {
            log.error("SSO 获取token异常",ex);
            response.getWriter().print("登录失败,获取单点登录用户信息异常！");
        }

    }

    @ApiOperation("oauth2 注销")
    @RequestMapping("/logout")
    public void ssoLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.sysUserService.logout(request);
        response.sendRedirect( this.oauth2Client.getLogoutUri());
    }


    @RequestMapping("/logout/notice")
    public void ssoLogoutNotice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            sysUserService.logout(request);
        } catch (Exception e) {
            log.error("SSO通知注销，但是注销异常");
        }

        String url = this.oauth2Client.getWebAddress();
        response.sendRedirect(url);
    }
}
