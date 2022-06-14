import ProLayout, {PageContainer} from '@ant-design/pro-layout';
import CommonConstant from "@/model/constant/CommonConstant";
import {Outlet} from "react-router-dom";
import React, {Dispatch, SetStateAction, useEffect, useState} from "react";
import {getAppNav} from "@/App";
import {Avatar, Dropdown, Menu} from "antd";
import {LogoutOutlined, UserOutlined} from "@ant-design/icons/lib";
import {logout} from "../../../util/UserUtil";
import {InDev} from "../../../util/CommonUtil";
import {execConfirm, ToastError, ToastSuccess} from "../../../util/ToastUtil";
import {userBaseInfo, userLogout} from "@/api/UserController";
import {useAppDispatch, useAppSelector} from "@/store";
import {connectWebSocket, IWebSocketMessage} from "../../../util/WebSocketUtil";
import {setLoadMenuFlag, setWebSocketMessage, setWebSocketStatus} from '@/store/commonSlice';
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {menuListForUser} from "@/api/MenuController";
import {setUserBaseInfo, setUserMenuList} from '@/store/userSlice';
import BaseMenuDO from "@/model/entity/BaseMenuDO";

export default function () {

    const appDispatch = useAppDispatch()
    const loadMenuFlag = useAppSelector((state) => state.common.loadMenuFlag) // 是否获取过菜单
    const userMenuList = useAppSelector((state) => state.user.userMenuList) // 用户菜单
    const [element, setElement] = useState<React.ReactNode>(null);
    const [pathname, setPathname] = useState<string>('')

    // 更新 redux里面 webSocket消息模板的值
    function doSetSocketMessage(param: IWebSocketMessage) {
        appDispatch(setWebSocketMessage(param))
    }

    // 更新 redux里面 webSocket的状态
    function doSetSocketStatus(param: boolean) {
        appDispatch(setWebSocketStatus(param))
    }

    // 设置：userMenuList
    function doSetUserMenuList(userMenuList: BaseMenuDO[]) {
        appDispatch(setUserMenuList(userMenuList))
        appDispatch(setLoadMenuFlag(true))
    }

    // 设置 element
    function doSetElement() {
        setElement(MainLayoutElement({pathname, setPathname, doSetUserMenuList}))
    }

    useEffect(() => {

        setPathname(window.location.pathname)

        doSetElement()

        if (!loadMenuFlag) {

            sessionStorage.setItem(SessionStorageKey.LOAD_MENU_FLAG, String(false))

            connectWebSocket(doSetSocketMessage, doSetSocketStatus) // 连接 webSocket

            userBaseInfo().then(res => {
                appDispatch(setUserBaseInfo(res.data))
            })

        } else {
            if (window.location.pathname === CommonConstant.MAIN_PATH) {
                userMenuList.some((item) => {
                    if (item.firstFlag && item.path) {
                        getAppNav()(item.path)
                    }
                    return item.firstFlag
                })
            }
        }
    }, [])

    return element
}

interface IMainLayoutElement {
    pathname: string
    setPathname: Dispatch<SetStateAction<string>>
    doSetUserMenuList: (data: BaseMenuDO[]) => void
}

// MainLayout组件页面
function MainLayoutElement(props: IMainLayoutElement) {

    return (
        <ProLayout
            className={"vh100"}
            title={CommonConstant.SYS_NAME}
            location={{
                pathname: props.pathname
            }}
            menu={{
                request: async () => {
                    let menuList
                    // 加载菜单
                    await menuListForUser().then(res => {
                        if (!res.data || !res.data.length) {
                            ToastError('暂未配置菜单，请联系管理员', 5)
                            logout()
                            return
                        }
                        menuList = res.data
                        props.doSetUserMenuList(res.data)
                        res.data.some((item) => {
                            if (item.firstFlag && item.path) {
                                getAppNav()(item.path)
                            }
                            return item.firstFlag
                        })
                    })
                    return menuList;
                },
            }}
            fixSiderbar={true}
            fixedHeader={true}
            menuItemRender={(item: BaseMenuDO, dom: React.ReactNode) => (
                <a
                    onClick={() => {
                        if (item.path) {
                            props.setPathname(item.path)
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
