import {getAppNav} from "@/App";
import CommonConstant from "../src/model/constant/CommonConstant";
import {ToastSuccess} from "./ToastUtil";

export function logout(msg ?: string) {
    localStorage.clear()
    sessionStorage.clear()
    getAppNav()(CommonConstant.LOGIN_PATH)
    if (msg) {
        ToastSuccess(msg)
    }
}
