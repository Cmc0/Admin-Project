package com.admin.im.service;

import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.dto.ImSessionPageDTO;
import com.admin.im.model.vo.ImSessionPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ImService {

    String send(ImSendDTO dto);

    Page<ImSessionPageVO> sessionPage(ImSessionPageDTO dto);

}
