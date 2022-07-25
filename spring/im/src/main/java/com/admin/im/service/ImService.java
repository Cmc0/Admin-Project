package com.admin.im.service;

import com.admin.im.model.document.ImFriendRequestDocument;
import com.admin.im.model.dto.ImFriendRequestDTO;
import com.admin.im.model.dto.ImFriendRequestHandlerDTO;
import com.admin.im.model.dto.ImFriendRequestPageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ImService {

    String friendRequest(ImFriendRequestDTO dto);

    String friendRequestHandler(ImFriendRequestHandlerDTO dto);

    Page<ImFriendRequestDocument> friendRequestPage(ImFriendRequestPageDTO dto);

}
