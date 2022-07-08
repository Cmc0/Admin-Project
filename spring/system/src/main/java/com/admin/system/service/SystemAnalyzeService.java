package com.admin.system.service;

import com.admin.system.model.vo.SystemAnalyzeActiveUserTrendVO;
import com.admin.system.model.vo.SystemAnalyzeActiveUserVO;
import com.admin.system.model.vo.SystemAnalyzeTrafficUsageVO;
import com.admin.system.model.vo.SystemAnalyzeUserVO;

import java.util.List;

public interface SystemAnalyzeService {

    SystemAnalyzeActiveUserVO activeUser();

    List<SystemAnalyzeActiveUserTrendVO> activeUserTrend();

    SystemAnalyzeUserVO user();

    List<SystemAnalyzeTrafficUsageVO> trafficUsage();

}
