package com.admin.websocket.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.websocket.model.dto.WebSocketPageDTO;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.model.vo.WebSocketPageVO;
import com.admin.websocket.model.vo.WebSocketRegisterVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface WebSocketService extends IService<WebSocketDO> {

    void offlineAllForCurrent();

    String retreatAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet);

    String retreatAndNoticeAll();

    WebSocketRegisterVO register(NotNullByte notNullByte);

    Page<WebSocketPageVO> myPage(WebSocketPageDTO dto);

    String changeType(NotNullByteAndId notNullByteAndId);

    void offlineByWebSocketIdSet(Set<Long> webSocketIdSet);
}
