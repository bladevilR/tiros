package org.jeecg.common.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author Jak
 * @date 2021/3/22 20:22
 * @version: V1.0
 */
public class TokenExpireException extends AuthenticationException {
    public TokenExpireException() {
        super("token已过期，请重新申请token");
    }
}
