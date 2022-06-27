import {getAppNav} from "@/App";
import CommonConstant from "../src/model/constant/CommonConstant";
import {ToastSuccess} from "./ToastUtil";
import {randomStr} from "./StrUtil";

export function logout(msg ?: string) {
    localStorage.clear()
    sessionStorage.clear()
    getAppNav()(CommonConstant.LOGIN_PATH)
    if (msg) {
        ToastSuccess(msg)
    }
}

// 随机昵称
export function randomNickname() {
    return '用户昵称' + randomStr(6).toUpperCase()
}
