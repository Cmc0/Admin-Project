import {Navigate} from "react-router-dom";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import CommonConstant from "@/model/constant/CommonConstant";

export default function () {
    const jwt = localStorage.getItem(LocalStorageKey.JWT);
    return <Navigate to={jwt ? CommonConstant.MAIN_PATH : CommonConstant.LOGIN_PATH}/>
}
