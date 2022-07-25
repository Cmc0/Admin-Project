package com.admin.im.service;

import com.admin.im.model.dto.ImFriendRequestDTO;
import com.admin.im.model.dto.ImFriendRequestHandlerDTO;

public interface ImService {

    String friendRequest(ImFriendRequestDTO dto);

    String friendRequestHandler(ImFriendRequestHandlerDTO dto);

}
