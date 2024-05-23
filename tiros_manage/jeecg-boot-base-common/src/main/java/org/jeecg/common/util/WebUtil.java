package org.jeecg.common.util;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Jak
 * @date 2021/3/22 15:59
 * @version: V1.0
 */
@Slf4j
public class WebUtil {
    /**
     * 返回json，并用result的编码作为状态码
     * @param response
     * @param result
     */
    public static void responseJsonResult(ServletResponse response, Result result,boolean replace) {
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json;charset=utf-8");
            if (replace) {
                httpResponse.setStatus(result.getCode());
            } else {
                httpResponse.setStatus(200);
            }
            PrintWriter out = httpResponse.getWriter();
            out.println(JSONUtil.toJsonStr(result));
            out.flush();
            out.close();
        } catch (Exception exception) {
            log.error("Response 输出JSON数据异常:", exception);
        }
    }
    /**
     * 返回json，指定状态码
     * @param response
     * @param result
     */
    public static void responseJsonResult(ServletResponse response, Result result,Integer status) {
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setStatus(status);
            PrintWriter out = httpResponse.getWriter();
            out.println(JSONUtil.toJsonStr(result));
            out.flush();
            out.close();
        } catch (Exception exception) {
            log.error("Response 输出JSON数据异常:", exception);
        }
    }
}
