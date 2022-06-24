import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface UserBaseInfoVO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    email?: string // 邮箱，会脱敏
    nickname?: string // 昵称
    passwordFlag?: boolean // 是否有密码，用于前端显示，修改密码/设置密码
}

// 用户-管理 用户基本信息
export function sysUserBaseInfo(config?: AxiosRequestConfig) {
    return $http.myPost<UserBaseInfoVO>('/sysUser/baseInfo', undefined, config)
}

// 用户-管理 退出登录
export function sysUserLogout(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/logout', undefined, config)
}

export interface SysUserPageDTO extends MyPageDTO {
    addAdminFlag?: boolean // 是否追加 admin账号，备注：pageSize == -1 时生效
    current?: number // 第几页
    email?: string // 邮箱
    enableFlag?: boolean // 正常/冻结
    id?: number // 主键id
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
}

export interface SysUserPageVO {
    avatarUrl?: string // 头像url
    createTime?: string // 创建时间
    email?: string // 邮箱，备注：会脱敏
    enableFlag?: boolean // 正常/冻结
    id?: number // 主键id
    nickname?: string // 昵称
}

// 用户-管理 分页排序查询
export function sysUserPage(form: SysUserPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserPageVO>('/sysUser/page', form, config)
}
