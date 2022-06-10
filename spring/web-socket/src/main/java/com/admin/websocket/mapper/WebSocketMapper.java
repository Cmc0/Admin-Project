package com.admin.websocket.mapper;

import com.admin.websocket.model.dto.WebSocketPageDTO;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.model.vo.WebSocketPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface WebSocketMapper extends BaseMapper<WebSocketDO> {

    // 分页排序查询
    Page<WebSocketPageVO> myPage(Page<WebSocketPageVO> page, @Param("dto") WebSocketPageDTO dto);
}




