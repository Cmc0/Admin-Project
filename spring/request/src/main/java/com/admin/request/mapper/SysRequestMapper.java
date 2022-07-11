package com.admin.request.mapper;

import com.admin.request.model.dto.SysRequestPageDTO;
import com.admin.request.model.entity.SysRequestDO;
import com.admin.request.model.vo.SysRequestAllAvgVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SysRequestMapper extends BaseMapper<SysRequestDO> {

    // 所有请求的平均耗时-增强：增加筛选项
    SysRequestAllAvgVO allAvgPro(@Param("dto") SysRequestPageDTO dto);

    // 所有请求的平均耗时
    SysRequestAllAvgVO allAvg();

}
