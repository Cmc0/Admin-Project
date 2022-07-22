package com.admin.im.service;

import com.admin.im.model.dto.*;
import com.admin.im.model.vo.ImContentPageVO;
import com.admin.im.model.vo.ImFriendRequestPageVO;
import com.admin.im.model.vo.ImSessionPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ImService {

    String send(ImSendDTO dto);

    Page<ImSessionPageVO> sessionPage(ImSessionPageDTO dto);

    Page<ImContentPageVO> contentPage(ImContentPageDTO dto);

    String friendRequest(ImFriendRequestDTO dto);

    Page<ImFriendRequestPageVO> friendRequestPage(ImFriendRequestPageDTO dto);

}
