package org.jeecg.common.aspect.annotation;

import org.jeecg.common.constant.CommonConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 操作日志注解
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/1/18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    String content() default "";

    int operateType() default CommonConstant.OPERATE_TYPE_1;

    boolean saveParam() default true;

    boolean saveToLogFile() default false;

    boolean saveToDB() default true;

}
