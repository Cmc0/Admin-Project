package com.admin.common.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 过滤器处理所有HTTP请求，并检查是否存在带有正确令牌的 Authorization标头，并获取用户权限
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String authorization = request.getHeader(BaseConstant.JWT_HEADER_KEY);
        // 如果请求头中没有 Authorization信息则直接放行了
        if (authorization == null || !authorization.startsWith(BaseConstant.JWT_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        // 如果请求头中有 jwt，则进行解析，并且设置授权信息
        UsernamePasswordAuthenticationToken authentication = getAuthentication(authorization, response);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        super.doFilterInternal(request, response, chain);
    }

    /**
     * 这里从 jwt中获取用户信息并新建一个 authentication
     * 备注：这里对用户状态，会进行全面的校验，所以后续的接口，都不需要校验用户的状态
     */
    @SneakyThrows
    private UsernamePasswordAuthenticationToken getAuthentication(String authorization, HttpServletResponse response) {

        String jwt = authorization.replace(BaseConstant.JWT_PREFIX, "");

        if (StrUtil.isBlank(jwt)) {
            return null;
        }

        // 判断 jwt是否存在于 redis中
        Boolean hasKey = MyJwtUtil.redisTemplate.hasKey(MyJwtUtil.generateRedisJwtHash(authorization));
        if (hasKey == null || !hasKey) {
            return loginExpired(response); // 提示登录过期，请重新登录
        }

        JWT jwtOf = JWT.of(jwt);

        String jwtSecretSuf = null;
        Long userId = Convert.toLong(jwtOf.getPayload("userId"));
        if (BaseConstant.ADMIN_ID.equals(userId)) {
            if (!BaseConfiguration.adminProperties.isAdminEnable()) {
                return null;
            }
        } else {
            // 如果不是 admin
            jwtSecretSuf = MyJwtUtil.getUserJwtSecretSufByUserId(userId);  // 通过 userId获取到 私钥后缀
            if (StrUtil.isBlank(jwtSecretSuf)) { // 除了 admin账号，每个账号都肯定有 jwtSecretSuf
                return null;
            }
        }

        jwtOf.setKey(MyJwtUtil.getJwtSecret(jwtSecretSuf).getBytes());

        // 验证算法
        if (!jwtOf.verify()) {
            return null;
        }

        // 异步：清理过期了，但是还是存在于 jwtUser set里面的 jwtHash
        ThreadUtil.execute(() -> MyJwtUtil.removeJwtHashForJwtUser(userId));

        try {
            // 校验时间字段：如果过期了，这里会抛出 ValidateException异常
            JWTValidator.of(jwtOf).validateDate(new Date());
        } catch (ValidateException e) {
            return loginExpired(response); // 提示登录过期，请重新登录
        }

        // 通过 userId 获取用户具有的权限
        List<SimpleGrantedAuthority> userRolesByToken = MyJwtUtil.getAuthSetByUserId(userId);

        return new UsernamePasswordAuthenticationToken(userId, null, userRolesByToken);

    }

    /**
     * 提示登录过期，请重新登录
     */
    private UsernamePasswordAuthenticationToken loginExpired(HttpServletResponse response) {
        ResponseUtil.out(response, BaseBizCodeEnum.LOGIN_EXPIRED);
        return null;
    }
}
