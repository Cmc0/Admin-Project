import {Navigate} from "react-router-dom";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import CommonConstant from "@/model/constant/CommonConstant";
import SessionStorageKey from "@/model/constant/SessionStorageKey";

export default function () {
    if (!sessionStorage.getItem(SessionStorageKey.ADMIN_REDIRECT_PATH)) {
        sessionStorage.setItem(SessionStorageKey.ADMIN_REDIRECT_PATH, window.location.pathname)
    }
    const jwt = localStorage.getItem(LocalStorageKey.JWT);
    return <Navigate to={jwt ? CommonConstant.MAIN_PATH : CommonConstant.LOGIN_PATH}/>
}
