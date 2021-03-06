import EmailNotBlankDTO from "@/model/dto/EmailNotBlankDTO";
import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface UserRegisterByEmailDTO {
    code?: string // 邮箱验证码
    email?: string // 邮箱
    origPassword?: string // 前端加密之后的原始密码
    password?: string // 前端加密之后的密码
}

// 用户-注册 邮箱-注册
export function userRegisterEmail(form: UserRegisterByEmailDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userRegister/email', form, config)
}

// 用户-注册 邮箱-注册-发送验证码
export function userRegisterEmailSendCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userRegister/email/sendCode', form, config)
}
