import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface SysRequestAllAvgVO {
    avg?: number // 请求的平均耗时（毫秒）
    count?: number // 请求的总数
}

// 接口请求-管理 所有请求的平均耗时
export function sysRequestAllAvg(config?: AxiosRequestConfig) {
    return $http.myPost<SysRequestAllAvgVO>('/sysRequest/allAvg', undefined, config)
}

export interface SysRequestPageDTO extends MyPageDTO {
    beginCreateTime?: string // 创建开始时间
    beginTimeNumber?: number // 耗时开始（毫秒）
    category?: number // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    createId?: number // 创建人id
    current?: number // 第几页
    endCreateTime?: string // 创建结束时间
    endTimeNumber?: number // 耗时结束（毫秒）
    ip?: string // ip
    name?: string // 接口名（备用）
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    uri?: string // 请求的uri
}

// 接口请求-管理 所有请求的平均耗时-增强：增加筛选项
export function sysRequestAllAvgPro(form: SysRequestPageDTO, config?: AxiosRequestConfig) {
    return $http.myPost<SysRequestAllAvgVO>('/sysRequest/allAvgPro', form, config)
}

export interface SysRequestPageVO {
    category?: string // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    errorMsg?: string // 失败信息
    id?: number // 主键id
    ip?: string // ip
    name?: string // 接口名（备用）
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    remark?: string // 描述/备注
    successFlag?: boolean // 请求是否成功
    timeNumber?: number // 耗时（毫秒）
    timeStr?: string // 耗时（字符串）
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    uri?: string // 请求的uri
    version?: number // 乐观锁
}

// 接口请求-管理 分页排序查询
export function sysRequestPage(form: SysRequestPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysRequestPageVO>('/sysRequest/page', form, config)
}
