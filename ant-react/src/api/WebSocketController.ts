import NotNullByte from "@/model/dto/NotNullByte";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import NotNullByteAndId from "@/model/dto/NotNullByteAndId";
import $http from "../../util/HttpUtil";

// webSocket 更改在线状态
export function webSocketChangeType(form: NotNullByteAndId) {
    return $http.myPost<string>('/webSocket/changeType', form)
}

// webSocket 全部强退
export function webSocketOfflineAll() {
    return $http.myPost<string>('/webSocket/offlineAll')
}

// webSocket 强退，通过 idSet
export function webSocketOfflineByIdSet(form: NotEmptyIdSet) {
    return $http.myPost<string>('/webSocket/offlineByIdSet', form)
}

export interface WebSocketPageDTO extends MyPageDTO {
    browser?: string // 浏览器和浏览器版本，用 / 分隔表示
    category?: 'true' | 'false' // 类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序
    current?: number // 第几页
    enableFlag?: 'true' | 'false' // 连接中/断开连接
    id?: number // 主键id
    ip?: string // ip
    mobileFlag?: 'true' | 'false' // 是否是移动端网页，true：是 false 否
    order?: MyOrderDTO // 排序字段
    os?: string // 操作系统
    pageSize?: number // 每页显示条数
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    server?: string // 本次 Websocket连接的服务器的 ip:port
    type?: 'true' | 'false' // 状态：1 在线 2 隐身
    userId?: number // 用户id
}

export interface WebSocketPageVO {
    browser?: string // 浏览器和浏览器版本，用 / 分隔表示
    category?: string // 类别
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: 'true' | 'false' // 是否逻辑删除
    enableFlag?: 'true' | 'false' // 连接中/断开连接
    id?: number // 主键id
    ip?: string // ip
    jwtHash?: string // jwtHash，用于匹配 redis中存储的 jwtHash
    mobileFlag?: 'true' | 'false' // 是否是移动端网页，true：是 false 否
    os?: string // 操作系统
    region?: string // IpUtil.getRegion() 获取到的 ip所处区域
    remark?: string // 描述/备注
    server?: string // 本次 Websocket连接的服务器的 ip:port
    type?: string // 在线状态
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    userId?: number // 用户id
    userName?: string // 用户名
}

// webSocket 分页排序查询
export function webSocketPage(form: WebSocketPageDTO) {
    return $http.myProPagePost<WebSocketPageVO>('/webSocket/page', form)
}

export interface WebSocketRegVO {
    code?: string // WebSocket 连接码，备注：只能使用一次
    webSocketUrl?: string // WebSocket 连接地址，ip:port
}

// webSocket 获取 webSocket连接地址和随机码
export function webSocketReg(form: NotNullByte) {
    return $http.myPost<WebSocketRegVO>('/webSocket/reg', form)
}
