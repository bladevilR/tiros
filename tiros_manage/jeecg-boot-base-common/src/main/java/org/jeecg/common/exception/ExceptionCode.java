package org.jeecg.common.exception;

/**
 * <p>
 * 业务异常枚举
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-12-23
 */
public enum ExceptionCode {

    UNKNOWN_ERROR(9000, "未知异常"),
    USERNAME_OR_PASSWORD_ERROR(10000, "用户名或密码错误"),
    USERNAME_AUTH_CODE_ERROR(10001, "用户名验证码错误"),
    ORDER_TASK_NOT_FINISH(9001, "工单任务未完成"),
    ORDER_NOT_COMMIT(9002, "工单未提交不能关闭"),
    ILLEGAL_USER_ERROR(9003, "用户不合法"),
    ;

    private final int code;
    private final String message;

    ExceptionCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageByCode(int code) {
        for (ExceptionCode exceptionCode : values()) {
            if (exceptionCode.getCode() == code) {
                return exceptionCode.getMessage();
            }
        }
        return null;
    }

}
