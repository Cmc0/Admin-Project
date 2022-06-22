package com.admin.request.aop;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.jwt.JWT;
import com.admin.common.configuration.security.SecurityConfiguration;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.IpUtil;
import com.admin.common.util.RequestUtil;
import com.admin.request.model.entity.SysRequestDO;
import com.admin.request.service.SysRequestService;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
public class SysRequestAop {

    @Resource
    SysRequestService sysRequestService;
    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 切入点
     */
    @Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
    public void pointcut() {
    }

    @SneakyThrows
    @Around("pointcut() && @annotation(apiOperation)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ApiOperation apiOperation) {

        long timeNumber = System.currentTimeMillis();

        Object object = proceedingJoinPoint.proceed(); // 执行方法

        // 备注：如果执行方法时抛出了异常，那么代码不会往下执行

        timeNumber = System.currentTimeMillis() - timeNumber; // 耗时（毫秒）

        if (timeNumber == 0) {
            return object;
        }

        String uri = httpServletRequest.getRequestURI();

        // 这个路径不需要记录到数据库
        if (uri.equals(SecurityConfiguration.SYS_FILE_PUBLIC_DOWNLOAD_URL)) {
            return object;
        }

        String timeStr = DateUtil.formatBetween(timeNumber, BetweenFormatter.Level.MILLISECOND); // 耗时（字符串）

        SysRequestDO sysRequestDO = new SysRequestDO();

        Date date = new Date();

        sysRequestDO.setUri(uri);
        sysRequestDO.setTimeStr(timeStr);
        sysRequestDO.setTimeNumber(timeNumber);
        sysRequestDO.setName(apiOperation.value());
        sysRequestDO.setCategory(RequestUtil.getSysRequestCategoryEnum(httpServletRequest));
        sysRequestDO.setIp(ServletUtil.getClientIP(httpServletRequest));
        sysRequestDO.setRegion(IpUtil.getRegion(sysRequestDO.getIp()));

        // 登录时需要额外处理来获取 用户id，备注：这里都必须以 /login开头才行
        if (uri.startsWith("/login") && !uri.contains("/sendCode")) {
            ApiResultVO<String> apiResult = (ApiResultVO)object;
            JWT jwtOf = JWT.of(apiResult.getData().replace(BaseConstant.JWT_PREFIX, ""));
            Long userId = Convert.toLong(jwtOf.getPayload("userId"));
            sysRequestDO.setCreateId(userId);
            sysRequestDO.setUpdateId(userId);
        }

        // 存库
        sysRequestService.save(sysRequestDO);

        return object;
    }

}
