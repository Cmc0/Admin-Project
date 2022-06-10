import {BrowserRouter, NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {RouterMapKeyList} from "./router/RouterMap";
import MainLayoutRouterList from "@/router/MainLayoutRouterList";
import CommonConstant from "@/model/constant/CommonConstant";

export default function () {
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
                    {MainLayoutRouterList.map((item, index) => (
                        <Route
                            key={index}
                            path={item.path}
                            element={
                                <LoadElement element={item.element}/>
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
        const Element = RouterMap[props.element].element
        return <Element/>
    }
    return null
}
