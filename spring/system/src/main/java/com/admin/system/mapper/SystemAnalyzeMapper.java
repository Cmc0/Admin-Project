package com.admin.system.mapper;

import com.admin.system.model.vo.SystemAnalyzeActiveUserTrendVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserVO;
import com.admin.system.model.vo.SystemAnalyzeTrafficUsageVO;
import com.admin.system.model.vo.SystemAnalyzeUserVO;

import java.util.List;

public interface SystemAnalyzeMapper {

    // 活跃人数分析
    SystemAnalyzeActiveUserVO activeUser();

    // 活跃人数走势
    List<SystemAnalyzeActiveUserTrendVO> activeUserTrend();

    // 用户分析
    SystemAnalyzeUserVO user();

    // 流量占用情况
    List<SystemAnalyzeTrafficUsageVO> trafficUsage();

}
