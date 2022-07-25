package com.admin.im.service;

import com.admin.im.model.document.ImFriendRequestDocument;
import com.admin.im.model.document.ImMessageDocument;
import com.admin.im.model.dto.*;
import com.admin.im.model.vo.ImSessionPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ImService {

    String friendRequest(ImFriendRequestDTO dto);

    String friendRequestHandler(ImFriendRequestHandlerDTO dto);

    Page<ImFriendRequestDocument> friendRequestPage(ImFriendRequestPageDTO dto);

    String send(ImSendDTO dto);

    Page<ImSessionPageVO> sessionPage(ImSessionPageDTO dto);

    Page<ImMessageDocument> messagePage(ImMessagePageDTO dto);
}
