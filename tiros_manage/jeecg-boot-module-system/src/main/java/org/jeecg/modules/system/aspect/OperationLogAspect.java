package org.jeecg.modules.system.aspect;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.OperationLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.IPUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.service.ISysLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 * 操作日志切面
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/1/18
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private ISysLogService sysLogService;

    @Pointcut("@annotation(org.jeecg.common.aspect.annotation.OperationLog)")
    private void logAspect() {
    }

    @Around("logAspect()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog operationLog = signature.getMethod().getAnnotation(OperationLog.class);
        if (null == operationLog) {
            operationLog = joinPoint.getTarget().getClass().getAnnotation(OperationLog.class);
        }
        if (null == operationLog) {
            return joinPoint.proceed();
        }
        int operateType = operationLog.operateType();
        if (CommonConstant.OPERATE_TYPE_1 == operateType) {
            // 查询的方法不记录日志，因为意义不大
            return joinPoint.proceed();
        }

        long startTimeMillis = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long endTimeMillis = System.currentTimeMillis();
        long costTime = endTimeMillis - startTimeMillis;
        Date startTime = new Date(startTimeMillis);

        String controllerName = signature.getDeclaringTypeName();
        controllerName = controllerName.substring(controllerName.lastIndexOf(".") + 1);
        String methodName = signature.getName();
        String controllerMethodName = controllerName + "." + methodName;

        String argsString = null;
        if (operationLog.saveParam()) {
            // 过滤请求参数
            Object[] args = joinPoint.getArgs();
            Object[] filterArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (null == args[i]
                        || args[i] instanceof ServletRequest
                        || args[i] instanceof ServletResponse
                        || args[i] instanceof MultipartFile
                        || args[i] instanceof File) {
                    continue;
                }
                filterArgs[i] = args[i];
            }

            if (filterArgs.length > 0) {
                argsString = JSON.toJSONString(filterArgs);
                if (argsString.length() > 5000) {
                    argsString = "参数太大已忽略";
                }
            }
        } else {
            argsString = "根据配置已忽略";
        }

        // 获取请求信息
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        String requestMethod = request.getMethod();
        String ipAddr = IPUtils.getIpAddr(request);
        String requestURL = request.getRequestURL().toString();

        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        boolean success = true;
        String resultMessage = "";
        if (proceed instanceof Result) {
            Result result = (Result) proceed;
            success = result.isSuccess();
            resultMessage = result.getMessage();
        }
        if (operationLog.saveToLogFile()) {
            // 打印到日志文件
            log.info(String.format(
                    "请求路径：%s，请求方法：%s，请求参数：%s，请求人：%s，请求时间：%s，耗时：%s毫秒，执行结果：%s",
                    requestURL,
                    controllerMethodName,
                    argsString,
                    sysUser.getUsername() + sysUser.getRealname(),
                    DateUtils.datetimeFormat.get().format(startTime),
                    costTime,
                    success ? "成功" : ("失败，原因：" + resultMessage)));
        }

        if (operationLog.saveToDB()) {
            // 记录数据库
            String content = operationLog.content();
            if (StringUtils.isBlank(content)) {
                ApiOperation apiOperation = signature.getMethod().getAnnotation(ApiOperation.class);
                if (null != apiOperation) {
                    content = apiOperation.value();
                    Api classApi = joinPoint.getTarget().getClass().getAnnotation(Api.class);
                    if (null != classApi) {
                        content = Arrays.toString(classApi.tags()) + "-" + content;
                    }
                }
            }
            if (StringUtils.isBlank(content)) {
                content = controllerMethodName;
            }

            SysLog sysLog = new SysLog()
                    .setLogType(2)
                    .setLogContent(content)
                    .setOperateType(operationLog.operateType())
                    .setUserid(sysUser.getUsername())
                    .setUsername(sysUser.getRealname())
                    .setIp(ipAddr)
                    .setMethod(controllerMethodName)
                    .setRequestUrl(requestURL)
                    .setRequestParam(argsString)
                    .setRequestType(requestMethod)
                    .setCostTime(costTime)
                    .setCreateTime(startTime)
                    .setCreateBy(sysUser.getUsername());
            sysLogService.save(sysLog);
        }

        return proceed;
    }

}
