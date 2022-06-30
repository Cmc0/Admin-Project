package com.admin.request.service.impl;

import com.admin.common.model.constant.BaseConstant;
import com.admin.common.util.UserUtil;
import com.admin.request.mapper.SysRequestMapper;
import com.admin.request.model.dto.SysRequestPageDTO;
import com.admin.request.model.dto.SysRequestSelfLoginRecordPageDTO;
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
        return baseMapper.myPage(dto.getCreateTimeDescDefaultOrderPage(), dto);
    }

    /**
     * 所有请求的平均耗时-增强：增加筛选项
     */
    @Override
    public SysRequestAllAvgVO allAvgPro(SysRequestPageDTO dto) {
        return baseMapper.allAvgPro(dto);
    }

    /**
     * 所有请求的平均耗时
     */
    @Override
    public SysRequestAllAvgVO allAvg() {
        return baseMapper.allAvg();
    }

    /**
     * 当前用户：登录记录
     */
    @Override
    public Page<SysRequestPageVO> selfLoginRecord(SysRequestSelfLoginRecordPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysRequestPageDTO sysRequestPageDTO = new SysRequestPageDTO();
        sysRequestPageDTO.setUri(BaseConstant.USER_LOGIN_PATH);
        sysRequestPageDTO.setCreateId(currentUserId);
        sysRequestPageDTO.setCategory(dto.getCategory());
        sysRequestPageDTO.setRegion(dto.getRegion());
        sysRequestPageDTO.setIp(dto.getIp());
        sysRequestPageDTO.setCurrent(dto.getCurrent());
        sysRequestPageDTO.setPageSize(dto.getPageSize());
        sysRequestPageDTO.setOrder(dto.getOrder());

        return myPage(sysRequestPageDTO);
    }
}
