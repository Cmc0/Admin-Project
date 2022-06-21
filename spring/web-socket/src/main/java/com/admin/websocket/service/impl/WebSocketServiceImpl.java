package com.admin.websocket.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.common.model.entity.BaseEntity;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.*;
import com.admin.websocket.configuration.NettyServer;
import com.admin.websocket.mapper.WebSocketMapper;
import com.admin.websocket.model.dto.WebSocketPageDTO;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.model.enums.WebSocketTypeEnum;
import com.admin.websocket.model.vo.WebSocketPageVO;
import com.admin.websocket.model.vo.WebSocketRegisterVO;
import com.admin.websocket.service.WebSocketService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebSocketServiceImpl extends ServiceImpl<WebSocketMapper, WebSocketDO> implements WebSocketService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    JsonRedisTemplate<WebSocketDO> jsonRedisTemplate;

    /**
     * 启动项目 或者springboot销毁时：断开当前 WebSocket 的所有连接
     */
    @Override
    public void offlineAllForCurrent() {

        log.info("下线数据库的 WebSocket 连接，条件：server = {}", NettyServer.ipAndPort);

        lambdaUpdate().eq(WebSocketDO::getServer, NettyServer.ipAndPort).eq(BaseEntityThree::getEnableFlag, true)
            .set(BaseEntityThree::getEnableFlag, false).set(BaseEntity::getUpdateTime, new Date()).update(); // 更新

    }

    /**
     * 强退，通过 idSet
     */
    @Override
    public String retreatAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet) {

        List<WebSocketDO> webSocketDOList =
            lambdaQuery().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).eq(WebSocketDO::getEnableFlag, true)
                .groupBy(WebSocketDO::getJwtHash).select(WebSocketDO::getJwtHash).list();

        if (webSocketDOList.size() != 0) {

            Set<String> jwtHashSet = webSocketDOList.stream().map(WebSocketDO::getJwtHash).collect(Collectors.toSet());
            jsonRedisTemplate.delete(jwtHashSet);

            offlineByWebSocketIdSet(notEmptyIdSet.getIdSet()); // 更新

            // 并且给 消息中间件推送，进行下线操作
            KafkaUtil.loginExpired(notEmptyIdSet.getIdSet());

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 全部强退
     */
    @Override
    @Transactional
    public String retreatAndNoticeAll() {

        MyJwtUtil.removeAllJwtHash();

        List<WebSocketDO> webSocketDOList =
            lambdaQuery().eq(BaseEntityThree::getEnableFlag, true).select(BaseEntityTwo::getId).list();

        if (webSocketDOList.size() != 0) {

            Set<Long> webSocketIdSet = webSocketDOList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());

            lambdaUpdate().eq(BaseEntityThree::getEnableFlag, true).set(BaseEntityThree::getEnableFlag, false)
                .set(BaseEntityTwo::getUpdateTime, new Date()).update(); // 更新

            // 并且给 消息中间件推送，进行下线操作
            KafkaUtil.loginExpired(webSocketIdSet);

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 获取 webSocket连接地址和随机码
     */
    @Override
    public WebSocketRegisterVO register(NotNullByte notNullByte) {

        Byte value = notNullByte.getValue();

        WebSocketDO webSocketDO = new WebSocketDO(); // 存放到redis，用于匹配连接

        WebSocketTypeEnum webSocketTypeEnum = WebSocketTypeEnum.getByCode(value);
        if (webSocketTypeEnum == null) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        webSocketDO.setType(webSocketTypeEnum);

        setWebSocketForRegister(webSocketDO); // 设置：webSocketDO的其他属性值

        String uuid = IdUtil.simpleUUID();

        // 放到 redis里面，一分钟自动过期
        ValueOperations<String, WebSocketDO> ops = jsonRedisTemplate.opsForValue();

        ops.set(NettyServer.webSocketRegisterCodePreKey + uuid, webSocketDO, BaseConstant.MINUTE_1_EXPIRE_TIME,
            TimeUnit.MILLISECONDS);

        WebSocketRegisterVO webSocketRegisterVO = new WebSocketRegisterVO();
        webSocketRegisterVO.setWebSocketUrl(NettyServer.ipAndPort);
        webSocketRegisterVO.setCode(uuid);

        return webSocketRegisterVO;
    }

    /**
     * {@link com.admin.websocket.configuration.MyNettyWebSocketHandler#channelRead}
     * 设置：webSocketDO的其他属性值
     */
    private void setWebSocketForRegister(WebSocketDO webSocketDO) {

        webSocketDO.setUserId(UserUtil.getCurrentUserId());

        String ip = ServletUtil.getClientIP(httpServletRequest);
        webSocketDO.setIp(ip);

        String uaStr = httpServletRequest.getHeader(Header.USER_AGENT.getValue());

        UserAgent ua = UserAgentUtil.parse(uaStr);

        webSocketDO.setRegion(IpUtil.getRegion(ip));
        webSocketDO.setBrowser(ua.getBrowser().toString() + "/" + ua.getVersion());
        webSocketDO.setOs(ua.getOs().toString());
        webSocketDO.setMobileFlag(ua.isMobile());
        webSocketDO.setServer(NettyServer.ipAndPort);

        webSocketDO.setCategory(RequestUtil.getRequestCategoryEnum(httpServletRequest));

        // 备注：这里不用担心 jwt不存在的问题，因为 JwtAuthorizationFilter已经处理过了，所以这里一定会有 jwt
        webSocketDO.setJwtHash(MyJwtUtil
            .generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY), webSocketDO.getUserId(),
                webSocketDO.getCategory()));

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<WebSocketPageVO> myPage(WebSocketPageDTO dto) {

        return baseMapper.myPage(dto.getPage(), dto);
    }

    /**
     * 更改在线状态
     */
    @Override
    @Transactional
    public String changeType(NotNullByteAndId notNullByteAndId) {

        WebSocketDO webSocketDO = new WebSocketDO();

        webSocketDO.setId(notNullByteAndId.getId());
        webSocketDO.setType(WebSocketTypeEnum.getByCode(notNullByteAndId.getValue()));

        updateById(webSocketDO);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 离线：数据库的连接数据
     */
    @Override
    public void offlineByWebSocketIdSet(Set<Long> webSocketIdSet) {

        lambdaUpdate().in(BaseEntityTwo::getId, webSocketIdSet).eq(WebSocketDO::getEnableFlag, true)
            .set(WebSocketDO::getEnableFlag, false).set(BaseEntity::getUpdateTime, new Date()).update(); // 更新

    }

}




