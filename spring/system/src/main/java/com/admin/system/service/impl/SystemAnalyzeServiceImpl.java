package com.admin.system.service.impl;

import com.admin.system.mapper.SystemAnalyzeMapper;
import com.admin.system.model.vo.SystemAnalyzeActiveUserTrendVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserVO;
import com.admin.system.model.vo.SystemAnalyzeTrafficUsageVO;
import com.admin.system.model.vo.SystemAnalyzeUserAddAndDeleteVO;
import com.admin.system.service.SystemAnalyzeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SystemAnalyzeServiceImpl implements SystemAnalyzeService {

    @Resource
    SystemAnalyzeMapper baseMapper;

    /**
     * 活跃人数分析
     */
    @Override
    public SystemAnalyzeActiveUserVO activeUser() {
        return baseMapper.activeUser();
    }

    /**
     * 活跃人数走势
     */
    @Override
    public List<SystemAnalyzeActiveUserTrendVO> activeUserTrend() {
        return baseMapper.activeUserTrend();
    }

    /**
     * 用户新增和注销分析
     */
    @Override
    public SystemAnalyzeUserAddAndDeleteVO userAddAndDelete() {
        return baseMapper.newUser();
    }

    /**
     * 流量占用情况
     */
    @Override
    public SystemAnalyzeTrafficUsageVO trafficUsage() {
        return baseMapper.trafficUsage();
    }
}
