//package org.jeecg.modules.system.aspect;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Type;
//
///**
// * <p>
// * mapper执行时间切面
// * </p>
// *
// * @author zhaiyantao
// * @since 2021-01-07
// */
//@Slf4j
//@Aspect
//@Component
//public class MapperTimeAspect {
//
////    @Value("${spring.datasource.dynamic.datasource.master.url}")
////    private String dbUrl;
//
//    @Pointcut("execution(public * org.jeecg.modules..*.mapper..*.*(..)))")
//    private void mapper() {
//    }
//
//    @Around("mapper()")
//    public Object mapperTimeCount(ProceedingJoinPoint joinPoint) throws Throwable {
//        String mapperName = joinPoint.getSignature().getDeclaringTypeName();
//        String shortMapperName = mapperName.substring(mapperName.lastIndexOf(".") + 1);
//        if ("BaseMapper".equals(shortMapperName)) {
//            Type[] genericInterfaces = joinPoint.getTarget().getClass().getGenericInterfaces();
//            String typeName = genericInterfaces[0].getTypeName();
//            shortMapperName = typeName.substring(typeName.lastIndexOf(".") + 1);
//        }
//        String methodName = joinPoint.getSignature().getName();
//
//        long startTime = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long endTime = System.currentTimeMillis();
////        log.info("数据库地址" + dbUrl);
//        log.info("数据库操作" + shortMapperName + "." + methodName + "方法执行完毕，耗时" + (endTime - startTime) + "毫秒。");
//        return result;
//    }
//
//}
