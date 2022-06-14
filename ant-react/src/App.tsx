import {BrowserRouter, NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {RouterMapKeyList} from "./router/RouterMap";
import CommonConstant from "@/model/constant/CommonConstant";
import {useAppSelector} from "@/store";
import React from "react";

export default function () {

    const userMenuList = useAppSelector(
        (state) => state.user.userMenuList
    ).filter((item) => !item.linkFlag && item.router)

    return (
        <BrowserRouter>
            <Routes>
                {NoLoginRouterList.map((item, index) => (
                    <Route
                        key={index}
                        path={item.path}
                        element={
                            <LoadElement element={item.element}/>
                        }
                    />
                ))}
                <Route
                    path={CommonConstant.MAIN_PATH}
                    element={<LoadElement element="MainLayout"/>}
                >
                    {userMenuList.map((item, index) => (
                        <Route
                            key={index}
                            path={item.path}
                            element={
                                <LoadElement element={item.router}/>
                            }
                        />
                    ))}
                </Route>
                <Route
                    path="*"
                    element={<LoadElement element="Jump"/>}
                />
            </Routes>
        </BrowserRouter>
    )
}

let AppNav: NavigateFunction

export function getAppNav() {
    return AppNav
}

interface ILoadElement {
    element?: string
}

// 加载 element
function LoadElement(props: ILoadElement) {
    AppNav = useNavigate()
    if (props.element && RouterMapKeyList.includes(props.element)) {
        return React.createElement(RouterMap[props.element].element)
    }
    return null
}
