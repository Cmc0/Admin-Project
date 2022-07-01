import MyCodeToKeyDTO from "@/model/dto/MyCodeToKeyDTO";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import MyPageDTO from "@/model/dto/MyPageDTO";
import NotNullId from "@/model/dto/NotNullId";
import {AxiosRequestConfig} from "axios";
import NotEmptyIdSet from "@/model/dto/NotEmptyIdSet";
import $http from "../../util/HttpUtil";

// 用户-管理 批量注销用户
export function sysUserDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/deleteByIdSet', form, config)
}

export interface SysUserInfoByIdVO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    createId?: number // 创建人id
    createTime?: string // 创建时间
    delFlag?: boolean // 是否注销
    deptIdSet?: number[] // 部门 idSet
    email?: string // 邮箱
    enableFlag?: boolean // 正常/冻结
    id?: number // 主键id
    jobIdSet?: number[] // 岗位 idSet
    jwtSecretSuf?: string // 用户 jwt私钥后缀（simple uuid）
    nickname?: string // 昵称
    password?: string // 密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】
    remark?: string // 描述/备注
    roleIdSet?: number[] // 角色 idSet
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    uuid?: string // 该用户的 uuid，本系统使用 id，不使用 uuid
    version?: number // 乐观锁
}

// 用户-管理 通过主键id，查看详情
export function sysUserInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysUserInfoByIdVO>('/sysUser/infoById', form, config)
}

export interface SysUserInsertOrUpdateDTO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    deptIdSet?: number[] // 部门 idSet
    email?: string // 邮箱
    enableFlag?: boolean // 正常/冻结
    id?: number // 主键id
    jobIdSet?: number[] // 岗位 idSet
    nickname?: string // 昵称
    origPassword?: string // 前端加密之后的原始密码
    password?: string // 前端加密之后的密码
    roleIdSet?: number[] // 角色 idSet
}

// 用户-管理 新增/修改
export function sysUserInsertOrUpdate(form: SysUserInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/insertOrUpdate', form, config)
}

export interface SysUserPageDTO extends MyPageDTO {
    addAdminFlag?: boolean // 是否追加 admin账号，备注：pageSize == -1 时生效
    beginCreateTime?: string // 创建开始时间
    beginLastActiveTime?: string // 最近活跃开始时间
    current?: number // 第几页
    email?: string // 邮箱
    enableFlag?: boolean // 正常/冻结
    endCreateTime?: string // 创建结束时间
    endLastActiveTime?: string // 最近活跃结束时间
    nickname?: string // 昵称
    order?: MyOrderDTO // 排序字段
    pageSize?: number // 每页显示条数
    passwordFlag?: boolean // 是否有密码
}

export interface SysUserPageVO {
    avatarUrl?: string // 头像url
    createTime?: string // 创建时间
    email?: string // 邮箱，备注：会脱敏
    enableFlag?: boolean // 正常/冻结
    id?: number // 主键id
    lastActiveTime?: string // 最近活跃时间
    nickname?: string // 昵称
    passwordFlag?: boolean // 是否有密码
    updateTime?: string // 修改时间
}

// 用户-管理 分页排序查询
export function sysUserPage(form: SysUserPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysUserPageVO>('/sysUser/page', form, config)
}

// 用户-管理 刷新用户 jwt私钥后缀
export function sysUserRefreshJwtSecretSuf(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/refreshJwtSecretSuf', form, config)
}

// 用户-管理 批量重置头像
export function sysUserResetAvatar(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/resetAvatar', form, config)
}

export interface SysUserSelfBaseInfoVO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    email?: string // 邮箱，会脱敏
    nickname?: string // 昵称
    passwordFlag?: boolean // 是否有密码，用于前端显示，修改密码/设置密码
}

// 用户-管理 当前用户：用户基本信息
export function sysUserSelfBaseInfo(config?: AxiosRequestConfig) {
    return $http.myPost<SysUserSelfBaseInfoVO>('/sysUser/selfBaseInfo', undefined, config)
}

// 用户-管理 当前用户：注销
export function sysUserSelfDelete(form: FormData, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfDelete', form, config)
}

// 用户-管理 当前用户：注销，发送，邮箱验证码
export function sysUserSelfDeleteSendEmailCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfDelete/sendEmailCode', undefined, config)
}

// 用户-管理 当前用户：退出登录
export function sysUserSelfLogout(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfLogout', undefined, config)
}

// 用户-管理 当前用户：刷新jwt私钥后缀
export function sysUserSelfRefreshJwtSecretSuf(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfRefreshJwtSecretSuf', undefined, config)
}

export interface SysUserSelfUpdateBaseInfoDTO {
    avatarUrl?: string // 头像url
    bio?: string // 个人简介
    nickname?: string // 昵称
}

// 用户-管理 当前用户：修改用户基本信息
export function sysUserSelfUpdateBaseInfo(form: SysUserSelfUpdateBaseInfoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfUpdateBaseInfo', form, config)
}

export interface SysUserSelfUpdateEmailDTO {
    code?: string // 新邮箱，验证码
    email?: string // 邮箱
    key?: string // 旧邮箱，验证码兑换的 key
}

// 用户-管理 当前用户：修改邮箱
export function sysUserSelfUpdateEmail(form: SysUserSelfUpdateEmailDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfUpdateEmail', form, config)
}

// 用户-管理 当前用户：修改邮箱，发送，邮箱验证码
export function sysUserSelfUpdateEmailSendEmailCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfUpdateEmail/sendEmailCode', undefined, config)
}

// 用户-管理 当前用户：修改邮箱，发送，邮箱验证码，验证码兑换 key
export function sysUserSelfUpdateEmailSendEmailCodeCodeToKey(form: MyCodeToKeyDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfUpdateEmail/sendEmailCode/codeToKey', form, config)
}

export interface SysUserSelfUpdatePasswordDTO {
    code?: string // 邮箱验证码
    newOrigPassword?: string // 前端加密之后的原始密码，新密码
    newPassword?: string // 前端加密之后的，新密码
}

// 用户-管理 当前用户：修改密码
export function sysUserSelfUpdatePassword(form: SysUserSelfUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfUpdatePassword', form, config)
}

// 用户-管理 当前用户：修改密码，发送，邮箱验证码
export function sysUserSelfUpdatePasswordSendEmailCode(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/selfUpdatePassword/sendEmailCode', undefined, config)
}

export interface SysUserUpdatePasswordDTO {
    idSet?: number[] // 主键 idSet
    newOrigPassword?: string // 前端加密之后的原始密码，新密码
    newPassword?: string // 前端加密之后的，新密码
}

// 用户-管理 批量修改密码
export function sysUserUpdatePassword(form: SysUserUpdatePasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sysUser/updatePassword', form, config)
}
