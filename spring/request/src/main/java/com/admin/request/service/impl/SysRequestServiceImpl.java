package com.admin.request.service.impl;

import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.constant.BaseConstant;
import com.admin.request.mapper.SysRequestMapper;
import com.admin.request.model.dto.SysRequestPageDTO;
import com.admin.request.model.entity.SysRequestDO;
import com.admin.request.model.vo.SysRequestAllAvgVO;
import com.admin.request.model.vo.SysRequestPageVO;
import com.admin.request.service.SysRequestService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysRequestServiceImpl extends ServiceImpl<SysRequestMapper, SysRequestDO> implements SysRequestService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysRequestPageVO> myPage(SysRequestPageDTO dto) {
        return baseMapper
            .myPage(dto.getPage(), dto, BaseConfiguration.adminProperties.getAdminNickname(), BaseConstant.ADMIN_ID);
    }

    /**
     * 所有请求的平均耗时
     */
    @Override
    public SysRequestAllAvgVO allAvg() {
        return baseMapper.allAvg();
    }
}
