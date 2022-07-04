import MyCodeToKeyDTO from "@/model/dto/MyCodeToKeyDTO";
import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface UserSelfBaseInfoVO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    email?: string // 邮箱，会脱敏
    nickname?: string // 昵称
    passwordFlag?: boolean // 是否有密码，用于前端显示，修改密码/设置密码
}

// 用户-自我-管理 获取：当前用户，基本信息
export function userSelfBaseInfo(config?: AxiosRequestConfig) {
    return $http.myPost<UserSelfBaseInfoVO>('/userSelf/baseInfo', undefined, config)
}

// 用户-自我-管理 当前用户：注销
export function userSelfDelete(form: FormData, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/delete', form, config)
}

// 用户-自我-管理 当前用户：注销，发送，邮箱验证码
export function userSelfDeleteSendEmailCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/delete/sendEmailCode', undefined, config)
}

// 用户-自我-管理 当前用户：退出登录
export function userSelfLogout(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/logout', undefined, config)
}

// 用户-自我-管理 当前用户：刷新jwt私钥后缀
export function userSelfRefreshJwtSecretSuf(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/refreshJwtSecretSuf', undefined, config)
}

export interface UserSelfUpdateBaseInfoDTO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    nickname?: string // 昵称
}

// 用户-自我-管理 当前用户：基本信息：修改
export function userSelfUpdateBaseInfo(form: UserSelfUpdateBaseInfoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/updateBaseInfo', form, config)
}

export interface UserSelfUpdateEmailDTO {
    code?: string // 新邮箱，验证码
    email?: string // 邮箱
    key?: string // 旧邮箱，验证码兑换的 key
}

// 用户-自我-管理 当前用户：修改邮箱
export function userSelfUpdateEmail(form: UserSelfUpdateEmailDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/updateEmail', form, config)
}

// 用户-自我-管理 当前用户：修改邮箱，发送，邮箱验证码
export function userSelfUpdateEmailSendEmailCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/updateEmail/sendEmailCode', undefined, config)
}

// 用户-自我-管理 当前用户：修改邮箱，发送，邮箱验证码，验证码兑换 key
export function userSelfUpdateEmailSendEmailCodeCodeToKey(form: MyCodeToKeyDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/updateEmail/sendEmailCode/codeToKey', form, config)
}

export interface UserSelfUpdatePasswordDTO {
    code?: string // 邮箱验证码
    newOrigPassword?: string // 前端加密之后的原始密码，新密码
    newPassword?: string // 前端加密之后的，新密码
}

// 用户-自我-管理 当前用户：修改密码
export function userSelfUpdatePassword(form: UserSelfUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/updatePassword', form, config)
}

// 用户-自我-管理 当前用户：修改密码，发送，邮箱验证码
export function userSelfUpdatePasswordSendEmailCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userSelf/updatePassword/sendEmailCode', undefined, config)
}
