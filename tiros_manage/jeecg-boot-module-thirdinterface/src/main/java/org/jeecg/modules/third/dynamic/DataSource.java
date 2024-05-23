package org.jeecg.modules.third.dynamic;

import java.lang.annotation.*;

/**
 * <p>
 * 多数据源注解，该注解用于AOP选择数据源的时候做标识，即我们这里通过AOP读取到自定义的注解决定选择数据源
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    DataSourceEnum value() ;

}
