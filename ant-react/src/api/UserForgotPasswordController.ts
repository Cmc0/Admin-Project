import EmailNotBlankDTO from "@/model/dto/EmailNotBlankDTO";
import {AxiosRequestConfig} from "axios";
import $http from "../../util/HttpUtil";

export interface UserSelfForgotPasswordDTO {
    account?: string // 账号
    code?: string // 验证码
    newOrigPassword?: string // 前端加密之后的原始密码，新密码
    newPassword?: string // 前端加密之后的，新密码
}

// 用户-忘记密码 忘记密码，重置密码
export function userForgotPassword(form: UserSelfForgotPasswordDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userForgotPassword', form, config)
}

// 用户-忘记密码 忘记密码，发送，邮箱验证码
export function userForgotPasswordSendEmailCode(form: EmailNotBlankDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/userForgotPassword/sendEmailCode', form, config)
}
