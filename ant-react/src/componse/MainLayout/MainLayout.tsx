import ProLayout, {PageContainer} from '@ant-design/pro-layout';
import CommonConstant from "@/model/constant/CommonConstant";
import {Navigate, Outlet} from "react-router-dom";
import MainLayoutRouterList, {IMainLayoutRouterList, MainLayoutRouterPathList} from "@/router/MainLayoutRouterList";
import React, {useEffect, useState} from "react";
import {getAppNav} from "@/App";
import {Avatar, Dropdown, Menu} from "antd";
import {LogoutOutlined, UserOutlined} from "@ant-design/icons/lib";
import {logout} from "../../../util/UserUtil";
import {InDev} from "../../../util/CommonUtil";
import {execConfirm, ToastSuccess} from "../../../util/ToastUtil";
import {userLogout} from "@/api/UserController";
import {useAppDispatch, useAppSelector} from "@/redux";
import {connectWebSocket, IWebSocketMessage} from "../../../util/WebSocketUtil";
import {setWebSocketMessage, setWebSocketStatus} from '@/redux/commonSlice';
import SessionStorageKey from "@/model/constant/SessionStorageKey";

export default function () {

    const appDispatch = useAppDispatch()
    const loadMenuFlag = useAppSelector((state) => state.common.loadMenuFlag) // 是否获取过菜单

    // 更新 redux里面 webSocket的值
    function doSetSocketMessage(param: IWebSocketMessage) {
        appDispatch(setWebSocketMessage(param))
    }

    // 更新 redux里面 webSocket的状态
    function doSetSocketStatus(param: boolean) {
        appDispatch(setWebSocketStatus(param))
    }

    useEffect(() => {
        if (!loadMenuFlag) {

            sessionStorage.setItem(SessionStorageKey.LOAD_MENU_FLAG, String(false))

            connectWebSocket(doSetSocketMessage, doSetSocketStatus) // 连接 webSocket

        }
    }, [])

    if (window.location.pathname === CommonConstant.MAIN_PATH) {
        if (MainLayoutRouterPathList[0]) {
            return <Navigate to={MainLayoutRouterPathList[0]}/>
        }
    }

}

// MainLayout组件页面
function MainLayoutElement() {

    const [pathname, setPathname] = useState<string>()

    useEffect(() => {
        setPathname(window.location.pathname)
    }, [])

    return (
        <ProLayout
            className={"vh100"}
            title={CommonConstant.SYS_NAME}
            location={{
                pathname
            }}
            menu={{
                request: async () => {
                    return MainLayoutRouterList;
                },
            }}
            fixSiderbar={true}
            fixedHeader={true}
            menuItemRender={(item: IMainLayoutRouterList, dom: React.ReactNode) => (
                <a
                    onClick={() => {
                        if (item.path && item.element) {
                            setPathname(item.path)
                            getAppNav()(item.path)
                        }
                    }}
                >
                    {dom}
                </a>
            )}
            rightContentRender={() => (
                <>
                    <Dropdown overlay={<Menu items={[
                        {
                            key: 'personalCenter',
                            label: <a onClick={InDev}>
                                个人中心
                            </a>,
                            icon: <UserOutlined/>
                        },
                        {
                            key: 'logout',
                            label: <a
                                onClick={() => {
                                    execConfirm(() => {
                                        return userLogout().then((res) => {
                                            ToastSuccess(res.msg)
                                            logout()
                                        })
                                    }, undefined, "确定退出登录吗？")
                                }}
                            >
                                退出登录
                            </a>,
                            icon: <LogoutOutlined/>
                        },
                    ]}>

                    </Menu>}>
                        <Avatar className={"hand"} size="small" icon={<UserOutlined/>}/>
                    </Dropdown>
                </>
            )}
        >
            <PageContainer>
                <Outlet/>
            </PageContainer>
        </ProLayout>
    )
}
