import $http from "../../util/HttpUtil";

export interface UserBaseInfoVO {
    avatarUrl?: string // 头像url
    email?: string // 邮箱，会被脱敏
    nickname?: string // 昵称
    passwordFlag?: boolean // 是否有密码，用于前端显示，修改密码/设置密码
    personalStatement?: string // 个性签名
    phone?: string // 手机号，会被脱敏
}

// 用户-管理 用户基本信息
export function userBaseInfo() {
    return $http.myPost<UserBaseInfoVO>('/user/baseInfo')
}

// 用户-管理 退出登录
export function userLogout() {
    return $http.myPost<string>('/user/logout')
}
