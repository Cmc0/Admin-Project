import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface SystemAnalyzeActiveUserVO {
    dailyTotal?: number // 每日活跃人数
    yesterdayTotal?: number // 昨日活跃人数
}

// 平台系统-分析 活跃人数分析
export function systemAnalyzeActiveUser(config?: AxiosRequestConfig) {
    return $http.myPost<SystemAnalyzeActiveUserVO>('/systemAnalyze/activeUser', undefined, config)
}

export interface SystemAnalyzeActiveUserTrendVO {
    monthDataStr?: string // 年月，格式：2022年07月（字符串）
    total?: number // 该月活跃人数
}

// 平台系统-分析 活跃人数走势
export function systemAnalyzeActiveUserTrend(config?: AxiosRequestConfig) {
    return $http.myProTreePost<SystemAnalyzeActiveUserTrendVO>('/systemAnalyze/activeUserTrend', undefined, config)
}

export interface SystemAnalyzeTrafficUsageVO {
    categoryStr?: string // 请求类别
    total?: number // 该类别请求总数
}

// 平台系统-分析 流量占用情况
export function systemAnalyzeTrafficUsage(config?: AxiosRequestConfig) {
    return $http.myProTreePost<SystemAnalyzeTrafficUsageVO>('/systemAnalyze/trafficUsage', undefined, config)
}

export interface SystemAnalyzeUserVO {
    dailyAddTotal?: number // 每日新增用户
    dailyDeleteTotal?: number // 每日注销用户
    total?: number // 总用户数
    yesterdayAddTotal?: number // 昨日新增用户
    yesterdayDeleteTotal?: number // 昨日注销用户
}

// 平台系统-分析 用户分析
export function systemAnalyzeUser(config?: AxiosRequestConfig) {
    return $http.myPost<SystemAnalyzeUserVO>('/systemAnalyze/user', undefined, config)
}
