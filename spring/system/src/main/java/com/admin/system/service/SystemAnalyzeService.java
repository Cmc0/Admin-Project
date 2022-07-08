package com.admin.system.service;

import com.admin.system.model.vo.SystemAnalyzeActiveUserTrendVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserVO;
import com.admin.system.model.vo.SystemAnalyzeNewUserVO;
import com.admin.system.model.vo.SystemAnalyzeTrafficUsageVO;

public interface SystemAnalyzeService {

    SystemAnalyzeActiveUserVO activeUser();

    SystemAnalyzeActiveUserTrendVO activeUserTrend();

    SystemAnalyzeNewUserVO newUser();

    SystemAnalyzeTrafficUsageVO trafficUsage();

}
