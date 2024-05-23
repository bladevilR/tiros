package org.jeecg.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 字典解析忽略 注解
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DictIgnore {
}
