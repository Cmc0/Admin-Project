import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface UserLoginByPasswordDTO {
    account?: string // 账号：邮箱
    password?: string // 密码
    rememberMe?: boolean // 记住我
}

// 用户-登录 账号密码登录
export function userLoginPassword(form: UserLoginByPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userLogin/password', form, config)
}
