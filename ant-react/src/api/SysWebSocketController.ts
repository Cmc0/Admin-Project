import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import NotNullByte from "@/model/dto/NotNullByte";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import {AxiosRequestConfig} from "axios";
import NotNullByteAndId from "@/model/dto/NotNullByteAndId";
import $http from "../../util/HttpUtil";

// webSocket 更改在线状态
export function sysWebSocketChangeType(form: NotNullByteAndId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysWebSocket/changeType', form, config)
}

export interface SysWebSocketPageDTO extends MyPageDTO {
    beginCreateTime?: string // 创建开始时间
    browser?: string // 浏览器和浏览器版本，用 / 分隔表示
    category?: number // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    createId?: number // 创建人id
    current?: number // 第几页
    enableFlag?: boolean // 连接中/断开连接
    endCreateTime?: string // 创建结束时间
    id?: number // 主键id
    ip?: string // ip
    mobileFlag?: boolean // 是否是移动端网页，true：是 false 否
    order?: MyOrderDTO // 排序字段
    os?: string // 操作系统
    pageSize?: number // 每页显示条数
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    server?: string // 本次 Websocket连接的服务器的 ip:port
    type?: number // 状态：1 在线 2 隐身
}

export interface SysWebSocketDO {
    browser?: string // 浏览器和浏览器版本，用 / 分隔表示
    category?: number // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否逻辑删除
    enableFlag?: boolean // 连接中/断开连接
    id?: number // 主键id
    ip?: string // ip
    jwtHash?: string // jwtHash，用于匹配 redis中存储的 jwtHash
    mobileFlag?: boolean // 是否是移动端网页，true 是 false 否
    os?: string // 操作系统
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    remark?: string // 描述/备注
    server?: string // 本次 WebSocket 连接的服务器的 ip:port
    type?: number // 状态：1 在线 2 隐身
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
}

// webSocket 分页排序查询
export function sysWebSocketPage(form: SysWebSocketPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysWebSocketDO>('/sysWebSocket/page', form, config)
}

export interface SysWebSocketRegisterVO {
    code?: string // WebSocket 连接码，备注：只能使用一次
    webSocketUrl?: string // WebSocket 连接地址，ip:port
}

// webSocket 获取 webSocket连接地址和随机码
export function sysWebSocketRegister(form: NotNullByte, config?: AxiosRequestConfig) {
    return $http.myPost<SysWebSocketRegisterVO>('/sysWebSocket/register', form, config)
}

// webSocket 全部强退
export function sysWebSocketRetreatAll(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysWebSocket/retreatAll', undefined, config)
}

// webSocket 强退，通过 idSet
export function sysWebSocketRetreatByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysWebSocket/retreatByIdSet', form, config)
}
