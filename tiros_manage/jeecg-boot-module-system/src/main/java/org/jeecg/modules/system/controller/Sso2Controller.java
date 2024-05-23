package org.jeecg.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 新版SSO集成接口
 *
 * @author Jak
 * @date 2022/10/9 14:59
 * @version: V1.0
 */
@Slf4j
@Controller
@RequestMapping("/sso")
@Api(tags = "单点登录")
public class Sso2Controller {
    @ApiOperation("单点登录2")
    @RequestMapping("/login2")
    public void ssoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 判断当前是否已经登录
        boolean isLogin = false;
        String token = request.getParameter("token");
        if (StrUtil.isNotEmpty(token)) {
            String userName = JwtUtil.getUsername(token);
            isLogin = JwtUtil.verify(token, userName, null);
        }
        String content = "";
        if (isLogin) {
            // 已经登录了， 跳转到首页
            String messageStr = "{\\\"type\\\":\\\"isLogin\\\",\\\"data\\\":\\\"" + token + "\\\"}";
                    /*    String sendMessage="           try {\n"+
                                                         "            window.opener.postMessage(\""+ messageStr +"\", \"*\");\n" +
                                                         "           } catch(e) { console.error(e); }\n";*/
            String sendMessage = "           try {\n" +
                    "            window.parent.postMessage(\"" + messageStr + "\", \"*\");\n" +
                    "            } catch(e) { console.error(e); }\n";
            content = "" +
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
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(content);
        } else {
            // 没有登录，跳转到sso登录地址
            content = "" +
                    "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<body>\n" +
                    "登陆中...\n" +
                    "<script>\n" +
                    "    window.onload = function () {\n" +
                    "        setTimeout(function (){\n" +
                    "            window.location.href='http://localhost:8080/tiros/ssoclient/login'\n" +
                    "        },500)\n" +
                    "    }\n" +
                    "</script>\n" +
                    "</body>\n" +
                    "</html>";
            response.sendRedirect(request.getContextPath() + "/ssoclient/login");
        }


    }
}
