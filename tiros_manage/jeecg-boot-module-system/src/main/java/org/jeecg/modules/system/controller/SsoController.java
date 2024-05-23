package org.jeecg.modules.system.controller;

import EIAC.EAC.SSO.AppSSOBLL;
import EIAC.EAC.SSO.ReadConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Jak
 * @date 2021-03-30 10:20
 * @version: V1.0
 */
@Slf4j
@Controller
@RequestMapping("/sso")
@Api(tags = "单点登录")
public class SsoController {
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("单点登录1")
    @RequestMapping("/login")
    public void ssoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String content = this.PostSsoLogin();
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(content);
    }

    @RequestMapping("/callback")
    public Result<?> ssoCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        String IASID = ReadConfig.getString("IASID");
        String UserAccount = "";
        String Result = "";
        String ErrorDescription = "";
        String Authenticator = "";
        String TimeStamp = "";

        if (request.getParameter("UserAccount") != null) {
            UserAccount = request.getParameter("UserAccount");
        }
        if (request.getParameter("Result") != null) {
            Result = request.getParameter("Result");
        }
        if (request.getParameter("ErrorDescription") != null) {
            ErrorDescription = request.getParameter("ErrorDescription");
        }
        if (request.getParameter("Authenticator") != null) {
            Authenticator = request.getParameter("Authenticator");
        }
        if (request.getParameter("TimeStamp") != null) {
            TimeStamp = request.getParameter("TimeStamp");
        }

        if (StringUtils.isNotBlank(UserAccount)) {
            if (StringUtils.isNotBlank(Result) && !"0".equals(Result)) {
                // 验证不成功
            }
            try {
                if (AppSSOBLL.ValidateFromEAC(IASID, TimeStamp, UserAccount, Result, ErrorDescription, Authenticator)) {
                    // 验证通过
                    //set cookies 一定要设置？
                    // Cookie newCookie = new Cookie("UserAccount", UserAccount);
                    log.info("====================单点登录成功：" + UserAccount);
                    //判断有没有这个人
                    LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
                    query.eq(SysUser::getUsername, UserAccount);
                    List<SysUser> users = sysUserService.list(query);
                    if (users != null && users.size() > 0) {
                        SysUser user = users.get(0);
                        // 生成token
                        String token = JwtUtil.sign(user.getId(), user.getUsername(), user.getPassword());
                        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);

                        String messageStr = "{\\\"type\\\":\\\"token\\\",\\\"data\\\":\\\"" + token + "\\\"}";
                    /*    String sendMessage="           try {\n"+
                                                         "            window.opener.postMessage(\""+ messageStr +"\", \"*\");\n" +
                                                         "           } catch(e) { console.error(e); }\n";*/
                        String sendMessage = "           try {\n" +
                                "            window.parent.postMessage(\"" + messageStr + "\", \"*\");\n" +
                                "            } catch(e) { console.error(e); }\n";

                        String content = "" +
                                "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<body>\n" +
                                "登陆中...\n" +
                                "<script>\n" +
                                "    window.onload = function () {\n" +
                                "        setTimeout(function (){\n" +
                                sendMessage +
                                "            window.close();\n" +
                                "        },500)\n" +
                                "    }\n" +
                                "</script>\n" +
                                "</body>\n" +
                                "</html>";
                        response.getWriter().print(content);
                    } else {
                        response.getWriter().print("登录失败,架大修系统不存在该用户！");
                    }

                } else {
                    // 没有验证通过
                    //to do 自身登陆
                    // response.sendRedirect("http://172.18.3.90:8080/hectest/login.service");
                    log.error("====================单点登录失败：" + UserAccount);
                    response.getWriter().print("单点验证失败");
                }
            } catch (Exception ex) {
                log.error("sso验证异常：", ex);
                response.getWriter().print("单点验证异常");
            }
        }

        return null;
    }

    private String PostSsoLogin() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String IASID = ReadConfig.getString("IASID");
        String IASKey = ReadConfig.getString("IASKey");
        String TimeStamp = sdf.format(new Date());
        String SSOURL = ReadConfig.getString("SSOURL");
        String ReturnURL = ReadConfig.getString("CALLBACKURL");

        try {
            String content = AppSSOBLL.PostString1(IASID, TimeStamp, ReturnURL, null, SSOURL, IASKey);
            return content;
        } catch (Exception ex) {
            // return "CreateAuthenticator error:" + var9.getMessage() + "Authenticator error:" + Authenticator;
            ex.printStackTrace();
            return "后端异常：" + ex.getMessage();
        }
    }
}
