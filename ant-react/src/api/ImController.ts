import {AxiosRequestConfig} from "axios";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import $http from "../../util/HttpUtil";

export interface ImContentPageDTO extends MyPageDTO {
    current?: number // 第几页
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    sid?: string // undefined
}

export interface ImContentPageVO {
    content?: string // 内容
    contentType?: number // 内容类型
    createId?: number // 创建用户 id
    createTime?: string // 创建时间
    id?: string // elasticsearch id
    sid?: string // undefined
    toId?: number // 冗余字段
    toType?: number // 冗余字段
}

// 即时通讯-管理 即时通讯内容，分页排序查询
export function imContentPage(form: ImContentPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<ImContentPageVO>('/im/contentPage', form, config)
}

export interface ImFriendRequestDTO {
    content?: string // 好友申请内容
    toId?: number // 冗余字段
}

// 即时通讯-管理 好友申请
export function imFriendRequest(form: ImFriendRequestDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/im/friendRequest', form, config)
}

export interface ImFriendRequestHandlerDTO {
    id?: string // elasticsearch id
    result?: number // 处理结果
}

// 即时通讯-管理 好友申请，结果处理
export function imFriendRequestHandler(form: ImFriendRequestHandlerDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/im/friendRequestHandler', form, config)
}

export interface ImFriendRequestPageDTO extends MyPageDTO {
    current?: number // 第几页
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
}

export interface ImFriendRequestPageVO {
    content?: string // 好友申请内容
    createId?: number // 创建用户 id
    createTime?: string // 创建时间
    id?: string // elasticsearch id
    result?: number // 好友申请结果
    toId?: number // 冗余字段
    updateTime?: string // 更新时间
}

// 即时通讯-管理 好友申请，分页排序查询，备注：包含我的申请，以及对我的申请
export function imFriendRequestPage(form: ImFriendRequestPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<ImFriendRequestPageVO>('/im/friendRequestPage', form, config)
}

export interface ImSendDTO {
    content?: string // 发送的内容
    toId?: number // 给谁发送，c/gId，联系人/群组 id
    toType?: number // 即时通讯 发送对象类型，1 联系人 2 群组
}

// 即时通讯-管理 发送消息
export function imSend(form: ImSendDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/im/send', form, config)
}

export interface ImSessionPageDTO extends MyPageDTO {
    current?: number // 第几页
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
}

export interface ImSessionPageVO {
    content?: string // 内容
    contentType?: number // 内容类型
    createId?: number // 创建用户 id
    createTime?: string // 创建时间
    id?: string // elasticsearch id
    sid?: string // undefined
    toAvatarUrl?: string // 头像地址
    toId?: number // 冗余字段
    toNickname?: string // 昵称
    toType?: number // 冗余字段
}

// 即时通讯-管理 即时通讯会话，分页排序查询，备注：暂时不支持分页
export function imSessionPage(form: ImSessionPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<ImSessionPageVO>('/im/sessionPage', form, config)
}
