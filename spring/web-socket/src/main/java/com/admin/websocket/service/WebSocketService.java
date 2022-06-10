package com.admin.websocket.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.websocket.model.entity.WebSocketDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface WebSocketService extends IService<WebSocketDO> {

    void offlineAndNoticeBySocketIdSetAndUserId(Set<Long> socketIdSet, Long userId,
        RequestCategoryEnum requestCategoryEnum);

    void offlineBySocketIdSet(Set<Long> socketIdSet);

    void offlineByUserIdSet(Set<Long> userIdSet);

    void offlineAndNoticeByUserIdSet(Set<Long> userIdSet);

    void offlineAllForCurrent();

    String offlineAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet);

    String offlineAndNoticeAll();

    void offlineByUserIdSetForDeleteUser(Set<Long> idSet);

}
