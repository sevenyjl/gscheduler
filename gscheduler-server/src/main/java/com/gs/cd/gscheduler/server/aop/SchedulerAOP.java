package com.gs.cd.gscheduler.server.aop;

import cn.hutool.core.util.StrUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import com.gs.cd.gsnow.entity.GSnowCollector;
import lombok.extern.slf4j.Slf4j;
import org.apache.hive.service.rpc.thrift.TCLIService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/4/13 17:10
 * @Description
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class SchedulerAOP {

    @Autowired
    TenantCodeService tenantCodeService;

    @Pointcut("execution(public * com.gs.cd.gscheduler.server.controller.*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String tenantCode = request.getHeader(HttpHeadersParam.TENANT_CODE);
        if (StrUtil.isNotEmpty(tenantCode)) {
            tenantCodeService.check(tenantCode);
            return joinPoint.proceed();
        } else {
            return ApiResult.error("无效租户！");
        }
    }

}
