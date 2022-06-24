package com.admin.user.mapper;

import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.vo.SysUserPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface SysUserProMapper extends BaseMapper<SysUserDO> {

    // 分页排序查询
    Page<SysUserPageVO> myPage(@Param("page") Page<SysUserPageVO> page, @Param("dto") SysUserPageDTO dto);
}
