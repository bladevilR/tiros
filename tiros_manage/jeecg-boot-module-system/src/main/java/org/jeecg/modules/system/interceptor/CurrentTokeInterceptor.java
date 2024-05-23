package org.jeecg.modules.system.interceptor;

import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.threadlocal.ThreadLocalToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 存储token拦截器
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-05-02
 */
public class CurrentTokeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(JwtUtil.AuthKey);
        ThreadLocalToken.set(token);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalToken.clear();
    }

}
