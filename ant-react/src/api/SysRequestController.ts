import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import $http from "../../util/HttpUtil";

export interface SysRequestPageDTO extends MyPageDTO {
    beginTime?: string // 创建开始时间
    beginTimeNumber?: number // 耗时开始（毫秒）
    category?: boolean // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    createId?: number // 创建人id
    current?: number // 第几页
    endTime?: string // 创建结束时间
    endTimeNumber?: number // 耗时结束（毫秒）
    ip?: string // ip
    name?: string // 接口名（备用）
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    time?: string // 耗时（字符串）
    uri?: string // 请求的uri
}

export interface SysRequestPageVO {
    category?: string // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 启用/禁用
    id?: number // 主键id
    ip?: string // ip
    name?: string // 接口名（备用）
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    remark?: string // 描述/备注
    time?: string // 耗时（字符串）
    timeNumber?: number // 耗时（毫秒）
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    uri?: string // 请求的uri
    userName?: string // 用户名
    version?: number // 乐观锁
}

// 接口请求-管理 分页排序查询
export function sysRequestPage(form: SysRequestPageDTO) {
    return $http.myProPagePost<SysRequestPageVO>('/sysRequest/page', form)
}
