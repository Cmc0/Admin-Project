package com.admin.websocket.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.websocket.model.dto.WebSocketPageDTO;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.model.vo.WebSocketPageVO;
import com.admin.websocket.model.vo.WebSocketRegVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface WebSocketService extends IService<WebSocketDO> {

    void offlineAndNoticeBySocketIdSetAndUserId(Set<Long> webSocketIdSet, Long userId,
        RequestCategoryEnum requestCategoryEnum);

    void offlineByWebSocketIdSet(Set<Long> webSocketIdSet);

    void offlineByUserIdSet(Set<Long> userIdSet);

    void offlineAndNoticeByUserIdSet(Set<Long> userIdSet);

    void offlineAllForCurrent();

    String offlineAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet);

    String offlineAndNoticeAll();

    void offlineByUserIdSetForDeleteUser(Set<Long> idSet);

    WebSocketRegVO reg(NotNullByte notNullByte);

    Page<WebSocketPageVO> myPage(WebSocketPageDTO dto);

    String changeType(NotNullByteAndId notNullByteAndId);

}
