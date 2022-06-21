package com.admin.websocket.mapper;

import com.admin.websocket.model.dto.SysWebSocketPageDTO;
import com.admin.websocket.model.entity.SysWebSocketDO;
import com.admin.websocket.model.vo.SysSysWebSocketPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface SysWebSocketMapper extends BaseMapper<SysWebSocketDO> {

    // 分页排序查询
    Page<SysSysWebSocketPageVO> myPage(Page<SysSysWebSocketPageVO> page, @Param("dto") SysWebSocketPageDTO dto);
}




