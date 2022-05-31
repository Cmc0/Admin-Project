import {Navigate} from "react-router-dom";
import {MainLayoutRouterPathList} from "@/router/MainLayoutRouterList";

export default function () {
    return <Navigate to={MainLayoutRouterPathList[0]!}/>
}
