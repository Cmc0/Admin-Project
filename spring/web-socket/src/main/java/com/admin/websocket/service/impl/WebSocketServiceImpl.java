package com.admin.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.common.util.KafkaUtil;
import com.admin.common.util.MyJwtUtil;
import com.admin.websocket.configuration.NettyServer;
import com.admin.websocket.mapper.WebSocketMapper;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.service.WebSocketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebSocketServiceImpl extends ServiceImpl<WebSocketMapper, WebSocketDO> implements WebSocketService {

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
}




