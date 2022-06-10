package com.admin.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
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
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.IpUtil;
import com.admin.common.util.KafkaUtil;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.UserUtil;
import com.admin.websocket.configuration.NettyServer;
import com.admin.websocket.mapper.WebSocketMapper;
import com.admin.websocket.model.dto.WebSocketPageDTO;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.model.enums.WebSocketTypeEnum;
import com.admin.websocket.model.vo.WebSocketPageVO;
import com.admin.websocket.model.vo.WebSocketRegVO;
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
     * 离线用户：通过 socketId，并且进行 socket通知
     */
    @Override
    public void offlineAndNoticeBySocketIdSetAndUserId(Set<Long> socketIdSet, Long userId,
        RequestCategoryEnum requestCategoryEnum) {

        offlineBySocketIdSet(socketIdSet); // 先操作数据库

        // 移除 redis中的 jwtHash
        MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(userId, requestCategoryEnum, null, null);

        // 并且给 消息中间件推送，进行下线操作
        KafkaUtil.forcedOffline(socketIdSet, null);

    }

    /**
     * 离线用户：通过 socketId
     */
    @Override
    public void offlineBySocketIdSet(Set<Long> socketIdSet) {

        if (CollUtil.isEmpty(socketIdSet)) {
            return;
        }

        lambdaUpdate().in(WebSocketDO::getId, socketIdSet).eq(BaseEntityThree::getEnableFlag, true)
            .set(BaseEntityThree::getEnableFlag, false).set(BaseEntityTwo::getUpdateTime, new Date()).update();

    }

    /**
     * 离线用户：通过 userIdSet
     */
    @Override
    public void offlineByUserIdSet(Set<Long> userIdSet) {

        if (CollUtil.isEmpty(userIdSet)) {
            return;
        }

        lambdaUpdate().in(WebSocketDO::getUserId, userIdSet).eq(BaseEntityThree::getEnableFlag, true)
            .set(BaseEntityThree::getEnableFlag, false).set(BaseEntityTwo::getUpdateTime, new Date()).update();

    }

    /**
     * 离线用户：通过 userIdSet
     */
    @Override
    public void offlineAndNoticeByUserIdSet(Set<Long> userIdSet) {

        offlineByUserIdSet(userIdSet);

        // 移除 redis中的 jwtHash
        for (Long item : userIdSet) {
            MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(item, null, null, null);
        }

        // 并且给 消息中间件推送，进行下线操作
        KafkaUtil.forcedOffline(null, userIdSet);

    }

    /**
     * 启动项目 或者springboot销毁时：断开当前 WebSocket 的所有连接
     */
    @Override
    public void offlineAllForCurrent() {

        log.info("下线数据库的 webSocket 连接，条件：server = {}", NettyServer.ipAndPort);

        lambdaUpdate().eq(WebSocketDO::getServer, NettyServer.ipAndPort).eq(BaseEntityThree::getEnableFlag, true)
            .set(BaseEntityThree::getEnableFlag, false).update();

    }

    /**
     * 强退，通过 idSet
     */
    @Override
    @Transactional
    public String offlineAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet) {

        offlineBySocketIdSet(notEmptyIdSet.getIdSet());

        List<WebSocketDO> socketDbList = lambdaQuery().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet())
            .select(WebSocketDO::getJwtHash, WebSocketDO::getUserId).groupBy(WebSocketDO::getJwtHash).list();

        if (socketDbList.size() != 0) {
            for (WebSocketDO item : socketDbList) {
                // 清理：jwtUser set里面的 jwtHash，以及 jwtHash
                MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(item.getUserId(), null, item.getJwtHash(), null);
            }
        }

        // 并且给 消息中间件推送，进行下线操作
        KafkaUtil.loginExpired(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 全部强退
     */
    @Override
    @Transactional
    public String offlineAndNoticeAll() {

        MyJwtUtil.removeAllJwtHash();

        List<WebSocketDO> socketDbList =
            lambdaQuery().eq(BaseEntityThree::getEnableFlag, true).select(BaseEntityTwo::getId).list();

        Set<Long> socketIdSet = socketDbList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());

        if (socketIdSet.size() == 0) {
            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        }

        lambdaUpdate().eq(BaseEntityThree::getEnableFlag, true).set(BaseEntityThree::getEnableFlag, false)
            .set(BaseEntityTwo::getUpdateTime, new Date()).update();

        // 并且给 消息中间件推送，进行下线操作
        KafkaUtil.loginExpired(socketIdSet);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 在注销用户的时候，通过 userIdSet，下线用户
     */
    @Override
    public void offlineByUserIdSetForDeleteUser(Set<Long> userIdSet) {

        offlineByUserIdSet(userIdSet);

        // 并且给 消息中间件推送，进行下线操作
        KafkaUtil.delAccount(userIdSet);

    }

    /**
     * 获取 webSocket连接地址和随机码
     */
    @Override
    public WebSocketRegVO reg(NotNullByte notNullByte) {

        Byte value = notNullByte.getValue();

        WebSocketDO webSocketDO = new WebSocketDO(); // 存放到redis，用于匹配连接

        WebSocketTypeEnum webSocketTypeEnum = WebSocketTypeEnum.getByCode(value);
        if (webSocketTypeEnum == null) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        webSocketDO.setType(webSocketTypeEnum);

        setWebSocketForReg(webSocketDO); // 设置：webSocketDO的其他属性值

        String uuid = IdUtil.simpleUUID(); // uuid

        // 放到 redis里面，一分钟自动过期
        ValueOperations<String, WebSocketDO> ops = jsonRedisTemplate.opsForValue();

        ops.set(NettyServer.webSocketRegCodePreKey + uuid, webSocketDO, BaseConstant.MINUTE_1_EXPIRE_TIME,
            TimeUnit.MILLISECONDS); // key：PRE_LOCK_WEB_SOCKET_REG_CODE:ip:port:uuid

        WebSocketRegVO webSocketRegVO = new WebSocketRegVO();
        webSocketRegVO.setSocketUrl(NettyServer.ipAndPort);
        webSocketRegVO.setCode(uuid);

        return webSocketRegVO;
    }

    /**
     * {@link com.admin.websocket.configuration.MyNettyWebSocketHandler#channelRead}
     * 设置：webSocketDO的其他属性值
     */
    private void setWebSocketForReg(WebSocketDO webSocketDO) {

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

        // 备注：这里不用担心 jwt不存在的问题，因为 JwtAuthorizationFilter已经处理过了，所以这里一定会有 jwt
        webSocketDO
            .setJwtHash(MyJwtUtil.generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY)));

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

}




