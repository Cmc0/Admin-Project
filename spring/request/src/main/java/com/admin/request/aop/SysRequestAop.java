package com.admin.request.aop;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.jwt.JWT;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.IpUtil;
import com.admin.common.util.RequestUtil;
import com.admin.request.model.entity.SysRequestDO;
import com.admin.request.service.SysRequestService;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @Around("pointcut() && @annotation(apiOperation)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, ApiOperation apiOperation) throws Throwable {

        long timeNumber = System.currentTimeMillis();
        String uri = httpServletRequest.getRequestURI();

        SysRequestDO sysRequestDO = new SysRequestDO();

        // 这个路径不需要记录到数据库
        sysRequestDO.setUri(uri);
        sysRequestDO.setTimeStr("");
        sysRequestDO.setTimeNumber(0L);
        sysRequestDO.setName(apiOperation.value());
        sysRequestDO.setCategory(RequestUtil.getSysRequestCategoryEnum(httpServletRequest));
        sysRequestDO.setIp(ServletUtil.getClientIP(httpServletRequest));
        sysRequestDO.setRegion(IpUtil.getRegion(sysRequestDO.getIp()));
        sysRequestDO.setSuccessFlag(true);
        sysRequestDO.setErrorMsg("");

        Object object;
        try {
            object = proceedingJoinPoint.proceed(); // 执行方法，备注：如果执行方法时抛出了异常，那么代码不会往下执行
        } catch (Throwable throwable) {
            sysRequestDO.setSuccessFlag(false); // 设置：请求失败
            if (StrUtil.isBlank(throwable.getMessage())) {
                sysRequestDO.setErrorMsg(StrUtil.maxLength(throwable.getClass().getName(), 200));
            } else {
                sysRequestDO.setErrorMsg(StrUtil.maxLength(throwable.getMessage(), 200));
            }
            sysRequestService.save(sysRequestDO); // 更新
            throw throwable;
        }

        timeNumber = System.currentTimeMillis() - timeNumber; // 耗时（毫秒）
        String timeStr = DateUtil.formatBetween(timeNumber, BetweenFormatter.Level.MILLISECOND); // 耗时（字符串）

        sysRequestDO.setTimeStr(timeStr);
        sysRequestDO.setTimeNumber(timeNumber);

        // 登录时需要额外处理来获取 用户id
        if (uri.startsWith(BaseConstant.USER_LOGIN_PATH)) {
            ApiResultVO<String> apiResult = (ApiResultVO)object;
            JWT jwtOf = JWT.of(apiResult.getData().replace(BaseConstant.JWT_PREFIX, ""));
            Long userId = Convert.toLong(jwtOf.getPayload("userId"));
            sysRequestDO.setCreateId(userId);
            sysRequestDO.setUpdateId(userId);
        }

        sysRequestService.save(sysRequestDO); // 更新

        return object;
    }

}
