package com.admin.im.service.impl;

import com.admin.im.model.dto.ImPageDTO;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.vo.ImPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class ImServiceImpl implements ImService {

    /**
     * 发送消息
     */
    @Override
    public String send(ImSendDTO dto) {

        return null;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<ImPageVO> myPage(ImPageDTO dto) {
        return null;
    }

}
