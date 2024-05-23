package org.jeecg.modules.third.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 多数据源注解切面
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
@Component
@Slf4j
@Aspect
// 该切面应当先于 @Transactional 执行
@Order(-1)
public class DataSourceAspect {

    @Before("@annotation(dataSource))")
    public void switchDataSource(JoinPoint point, DataSource dataSource) {
        String className = point.getSignature().getDeclaringTypeName();
        String shortClassName = className.substring(className.lastIndexOf(".") + 1);
        String methodName = point.getSignature().getName();

        if (!DynamicDataSourceContextHolder.containDataSourceKey(dataSource.value().getValue())) {
//            log.info(String.format("数据源【%s】不存在，使用默认数据源【%s】", dataSource.value().getValue(), DynamicDataSourceContextHolder.getDataSourceKey()));
        } else {
            // 切换数据源
            DynamicDataSourceContextHolder.setDataSourceKey(dataSource.value().getValue());
//            log.info(String.format("方法%s.%s切换数据源为【%s】", shortClassName, methodName, dataSource.value().getValue()));
        }
    }

    @After("@annotation(dataSource))")
    public void restoreDataSource(JoinPoint point, DataSource dataSource) {
        String className = point.getSignature().getDeclaringTypeName();
        String shortClassName = className.substring(className.lastIndexOf(".") + 1);
        String methodName = point.getSignature().getName();

        // 将数据源置为默认数据源
        DynamicDataSourceContextHolder.clearDataSourceKey();
//        log.info(String.format("方法%s.%s重置数据源为【%s】", shortClassName, methodName, DynamicDataSourceContextHolder.getDataSourceKey()));
    }

}
