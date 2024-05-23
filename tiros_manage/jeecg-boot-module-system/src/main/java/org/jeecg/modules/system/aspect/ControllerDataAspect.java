//package org.jeecg.modules.system.aspect;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.shiro.SecurityUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.jeecg.common.api.vo.Result;
//import org.jeecg.common.system.vo.LoginUser;
//import org.jeecg.common.util.IPUtils;
//import org.jeecg.common.util.SpringContextUtils;
//import org.jeecg.modules.system.entity.SysLog;
//import org.jeecg.modules.system.service.ISysLogService;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//
///**
// * <p>
// * 拦截打印controller传输数据
// * </p>
// *
// * @author zhaiyantao
// * @since 2021/10/22
// */
//@Slf4j
//@Aspect
//@Component
//public class ControllerDataAspect {
//
//    @Resource
//    private ISysLogService sysLogService;
//
//
//    @Pointcut("execution(public * org.jeecg.modules.basemanage..*.controller..*.*(..)) || execution(public * org.jeecg.modules.board..*.controller..*.*(..)) || execution(public * org.jeecg.modules.dispatch..*.controller..*.*(..)) || execution(public * org.jeecg.modules.group..*.controller..*.*(..)) || execution(public * org.jeecg.modules.material..*.controller..*.*(..)) || execution(public * org.jeecg.modules.outsource..*.controller..*.*(..)) || execution(public * org.jeecg.modules.produce..*.controller..*.*(..)) || execution(public * org.jeecg.modules.quality..*.controller..*.*(..)) || execution(public * org.jeecg.modules.report..*.controller..*.*(..)) || execution(public * org.jeecg.modules.tiros..*.controller..*.*(..)) || execution(public * org.jeecg.modules.workflow..*.controller..*.*(..)) || execution(public * org.jeecg.modules.system..*.controller..*.*(..))")
//    private void requestAspect() {
//    }
//
//    @Around("requestAspect()")
//    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
//        String controllerName = joinPoint.getSignature().getDeclaringTypeName();
//        String shortControllerName = controllerName.substring(controllerName.lastIndexOf(".") + 1);
//        String methodName = joinPoint.getSignature().getName();
//        String shortControllerMethodName = shortControllerName + "." + methodName;
//        Object[] args = joinPoint.getArgs();
//
//        long startTime = System.currentTimeMillis();
//        Object proceed = joinPoint.proceed();
//        long endTime = System.currentTimeMillis();
//        long time = endTime - startTime;
//
//        // 特定方法不打印参数，因为参数太多
//        boolean needArgs = true;
//        if ("saveFormDataRecordResult".equals(methodName)
//                || "saveFormWorkRecordExcelData".equals(methodName)
//                || "saveOrUpdatePlanTask".equals(methodName)
//                || "updateTaskNoAndWbs".equals(methodName)
//                || "BuBaseFormTemplateController.save".equals(shortControllerMethodName)
//                || "BuBaseFormTemplateController.edit".equals(shortControllerMethodName)) {
//            needArgs = false;
//        }
//        String argsString = "";
//        if (needArgs) {
//            // 过滤请求参数
//            Object[] filterArgs = new Object[args.length];
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
//                    continue;
//                }
//                filterArgs[i] = args[i];
//            }
//            argsString = JSON.toJSONString(filterArgs);
//        }
//
//        // 获取登录人信息
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//
//        if (proceed instanceof Result) {
//            // 打印
//            Result result = (Result) proceed;
//            String requestInfo;
//            if (result.isSuccess()) {
//                requestInfo = String.format(
//                        "业务controller方法：%s执行成功，请求参数：%s，执行时间：%s，操作人：%s",
//                        shortControllerMethodName,
//                        argsString,
//                        time + "毫秒",
//                        sysUser.getUsername() + sysUser.getRealname()
//                );
//            } else {
//                requestInfo = String.format(
//                        "业务controller方法：%s执行失败，请求参数：%s，执行时间：%s，操作人：%s，失败提示消息：%s",
//                        shortControllerMethodName,
//                        argsString,
//                        time + "毫秒",
//                        sysUser.getUsername() + sysUser.getRealname(),
//                        result.getMessage()
//                );
//            }
//            log.info(requestInfo);
//
//            // 记录数据库
//            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
//            String requestMethod = request.getMethod();
//            if (!"GET".equals(requestMethod)) {
//                // 非get方法，才保存日志记录
//                SysLog sysLog = new SysLog()
//                        .setLogType(2)
//                        .setLogContent(shortControllerMethodName)
//                        .setOperateType(null)
//                        .setUserid(sysUser.getUsername())
//                        .setUsername(sysUser.getRealname())
//                        .setIp(IPUtils.getIpAddr(request))
//                        .setMethod(shortControllerMethodName)
//                        .setRequestUrl(request.getRequestURL().toString())
//                        .setRequestParam(argsString)
//                        .setRequestType(requestMethod)
//                        .setCostTime(time)
//                        .setCreateTime(new Date(startTime))
//                        .setCreateBy(sysUser.getUsername());
//                sysLogService.save(sysLog);
//            }
//        }
//
//        return proceed;
//    }
//
//}
