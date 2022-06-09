import {Navigate} from "react-router-dom";
import LocalStorageKey from "@/model/constant/LocalStorageKey";

export default function () {
    const jwt = localStorage.getItem(LocalStorageKey.JWT);
    return <Navigate to={jwt ? "/main" : "/login"}/>
}
