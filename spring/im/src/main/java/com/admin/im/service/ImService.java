package com.admin.im.service;

import com.admin.im.model.dto.ImPageDTO;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.vo.ImPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ImService {

    String send(ImSendDTO dto);

    Page<ImPageVO> myPage(ImPageDTO dto);

}
