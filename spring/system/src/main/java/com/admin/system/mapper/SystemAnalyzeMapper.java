package com.admin.system.mapper;

import com.admin.system.model.vo.SystemAnalyzeActiveUserTrendVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserVO;
import com.admin.system.model.vo.SystemAnalyzeNewUserVO;
import com.admin.system.model.vo.SystemAnalyzeTrafficUsageVO;

public interface SystemAnalyzeMapper {

    // 活跃人数分析
    SystemAnalyzeActiveUserVO activeUser();

    // 活跃人数走势
    SystemAnalyzeActiveUserTrendVO activeUserTrend();

    // 新增用户分析
    SystemAnalyzeNewUserVO newUser();

    // 流量占用情况
    SystemAnalyzeTrafficUsageVO trafficUsage();

}
