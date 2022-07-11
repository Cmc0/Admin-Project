package com.admin.request.service;

import com.admin.request.model.dto.SysRequestPageDTO;
import com.admin.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.admin.request.model.entity.SysRequestDO;
import com.admin.request.model.vo.SysRequestAllAvgVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysRequestService extends IService<SysRequestDO> {

    Page<SysRequestDO> myPage(SysRequestPageDTO dto);

    SysRequestAllAvgVO allAvgPro(SysRequestPageDTO dto);

    SysRequestAllAvgVO allAvg();

    Page<SysRequestDO> selfLoginRecord(SysRequestSelfLoginRecordPageDTO dto);
}
