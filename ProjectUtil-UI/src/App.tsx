import {BrowserRouter, NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {RouterMapKeyList} from "./router/RouterMap";
import MainLayoutRouterList from "@/router/MainLayoutRouterList";

export default function App() {
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
                    path="/main"
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
            </Routes>
        </BrowserRouter>
    )
}

let AppNav: NavigateFunction

export function getAppNav() {
    return AppNav
}

interface ILoadElement {
    element: string
}

// 加载 element
function LoadElement(props: ILoadElement) {
    AppNav = useNavigate()
    if (RouterMapKeyList.includes(props.element)) {
        const Element = RouterMap[props.element].element
        return <Element/>
    }
    return null
}
