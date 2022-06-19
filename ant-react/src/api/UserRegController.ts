import EmailNotBlankDTO from "@/model/dto/EmailNotBlankDTO";
import $http from "../../util/HttpUtil";

export interface UserRegByEmailDTO {
    code?: string // 邮箱验证码
    email?: string // 邮箱
    origPassword?: string // 前端加密之后的原始密码
    password?: string // 前端加密之后的密码
}

// 用户-注册 邮箱-注册
export function userRegEmail(form: UserRegByEmailDTO) {
    return $http.myPost<string>('/userReg/email', form)
}

// 用户-注册 邮箱-注册-发送验证码
export function userRegEmailSendCode(form: EmailNotBlankDTO) {
    return $http.myPost<string>('/userReg/email/sendCode', form)
}
