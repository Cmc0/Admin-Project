package com.admin.common.mapper;

import com.admin.common.model.vo.SyncEntityFromDbVO;

import java.util.List;

public interface SyncEntityFromDbMapper {

    // 获取：所有字段信息
    List<SyncEntityFromDbVO> getAllColumnList();

}
