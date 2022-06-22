import ProLayout, {PageContainer} from '@ant-design/pro-layout';
import CommonConstant from "@/model/constant/CommonConstant";
import {Outlet} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {getAppNav} from "@/App";
import {Avatar, Badge, Dropdown, Menu, Space} from "antd";
import {LogoutOutlined, UserOutlined, WarningFilled} from "@ant-design/icons/lib";
import {logout} from "../../../util/UserUtil";
import {InDev} from "../../../util/CommonUtil";
import {execConfirm, ToastError, ToastSuccess} from "../../../util/ToastUtil";
import {useAppDispatch, useAppSelector} from "@/store";
import {connectWebSocket, IWebSocketMessage} from "../../../util/WebSocketUtil";
import {setWebSocketMessage, setWebSocketStatus} from '@/store/commonSlice';
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {setUserBaseInfo, setUserMenuList} from '@/store/userSlice';
import SysMenuDO from "@/model/entity/SysMenuDO";
import {ListToTree} from "../../../util/TreeUtil";
import {RouterMapKeyList} from "@/router/RouterMap";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {sysUserBaseInfo, sysUserLogout} from "@/api/SysUserController";
import {sysMenuListForUser} from "@/api/SysMenuController";
import {getWebSocketType, setWebSocketType, TWebSocketType} from "@/model/constant/LocalStorageKey";
import {sysWebSocketChangeType} from "@/api/SysWebSocketController";

// 前往：第一个页面
function goFirstPage(menuList: SysMenuDO[]) {

    if (window.location.pathname !== CommonConstant.MAIN_PATH) {
        return
    }

    const adminRedirectPath = sessionStorage.getItem(SessionStorageKey.ADMIN_REDIRECT_PATH);
    if (adminRedirectPath) {
        sessionStorage.removeItem(SessionStorageKey.ADMIN_REDIRECT_PATH)
        if (menuList.some(item => item.path === adminRedirectPath)) {
            return getAppNav()(adminRedirectPath)
        }
    }

    menuList.some((item) => {
        if (item.firstFlag && item.path) {
            getAppNav()(item.path)
        }
        return item.firstFlag
    })
}

export default function () {

    const appDispatch = useAppDispatch()
    const userMenuList = useAppSelector((state) => state.user.userMenuList)
    const loadMenuFlag = useAppSelector((state) => state.user.loadMenuFlag)
    const [element, setElement] = useState<React.ReactNode>(null);

    // 更新 redux里面 webSocket消息模板的值
    function doSetSocketMessage(param: IWebSocketMessage) {
        appDispatch(setWebSocketMessage(param))
    }

    // 更新 redux里面 webSocket的状态
    function doSetSocketStatus(param: boolean) {
        appDispatch(setWebSocketStatus(param))
    }

    // 设置 element
    function doSetElement(userMenuList: SysMenuDO[]) {
        if (element == null) {
            setElement(<MainLayoutElement userMenuList={userMenuList}/>)
        }
    }

    useEffect(() => {

        if (loadMenuFlag) {
            // 开发时才会用到 ↓
            doSetElement(userMenuList)
            goFirstPage(userMenuList)
            // 开发时才会用到 ↑
            return
        }

        sessionStorage.setItem(SessionStorageKey.LOAD_MENU_FLAG, String(false))

        connectWebSocket(doSetSocketMessage, doSetSocketStatus) // 连接 webSocket

        sysUserBaseInfo().then(res => {
            appDispatch(setUserBaseInfo(res.data))
        })

        // 加载菜单
        sysMenuListForUser().then(res => {
            if (!res.data || !res.data.length) {
                ToastError('暂未配置菜单，请联系管理员', 5)
                logout()
                return
            }
            appDispatch(setUserMenuList(res.data))
            doSetElement(res.data)
            goFirstPage(res.data)
        })

    }, [])

    return element
}

interface IMainLayoutElement {
    userMenuList: SysMenuDO[]
}

// MainLayout组件页面
function MainLayoutElement(props: IMainLayoutElement) {

    const [pathname, setPathname] = useState<string>('')
    const webSocketStatus = useAppSelector((state) => state.common.webSocketStatus)

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
                    let userMenuListTemp: SysMenuDO[] = JSON.parse(JSON.stringify(props.userMenuList));
                    userMenuListTemp = userMenuListTemp.filter(item => item.showFlag);
                    userMenuListTemp.forEach(item => {
                        if (item.icon) {
                            // @ts-ignore
                            item.icon = <MyIcon icon={item.icon}/>
                        }
                    })
                    return ListToTree(userMenuListTemp, true, 0, 'routes');
                },
            }}
            fixSiderbar={true}
            fixedHeader={true}
            menuItemRender={(item: SysMenuDO, dom: React.ReactNode) => (
                <a
                    onClick={() => {
                        if (item.path && item.router) {
                            if (RouterMapKeyList.includes(item.router)) {
                                if (item.linkFlag) {
                                    window.open(item.path, '_blank')
                                } else {
                                    setPathname(item.path)
                                    getAppNav()(item.path)
                                }
                            } else {
                                InDev()
                            }
                        }
                    }}
                >
                    <>
                        {dom}
                        {(item.router && !RouterMapKeyList.includes(item.router)) &&
                        <WarningFilled className={"warning2 m-l-4"}/>
                        }
                    </>
                </a>
            )}
            rightContentRender={() => (
                <Space size={20}>
                    <Dropdown overlay={<Menu items={[
                        {
                            key: '1',
                            label: <a onClick={() => doSysWebSocketChangeType('1')}>
                                我在线上
                            </a>,
                            icon: <Badge status={"success"}/>
                        },
                        {
                            key: '2',
                            label: <a onClick={() => doSysWebSocketChangeType('2')}>
                                隐身
                            </a>,
                            icon: <Badge status={"warning"}/>
                        },
                    ]}/>}>
                        <Badge
                            className={"hand"}
                            status={webSocketStatus ? (getWebSocketType() === '1' ? 'success' : 'warning') : 'default'}
                            text={webSocketStatus ? (getWebSocketType() === '1' ? '在线' : '隐身') : '离线'}
                        />
                    </Dropdown>

                    <Dropdown overlayClassName={"body-bc"} overlay={<Menu items={[
                        {
                            key: '1',
                            label: <a onClick={InDev}>
                                个人中心
                            </a>,
                            icon: <UserOutlined/>
                        },
                        {
                            key: '2',
                            label: <a
                                onClick={() => {
                                    execConfirm(() => {
                                        return sysUserLogout().then((res) => {
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
                    ]}/>}>
                        <Avatar className={"hand"} size="small" src={"https://joeschmoe.io/api/v1/random"}/>
                    </Dropdown>
                </Space>
            )}
        >
            <PageContainer>
                <Outlet/>
            </PageContainer>
        </ProLayout>
    )
}

function doSysWebSocketChangeType(value: TWebSocketType) {

    const webSocketId = sessionStorage.getItem(SessionStorageKey.WEB_SOCKET_ID);

    if (!webSocketId) {
        ToastError("切换状态失败，请刷新页面")
        return
    }

    sysWebSocketChangeType({id: Number(webSocketId), value: Number(value)}).then(res => {
        ToastSuccess(res.msg)
        setWebSocketType(value)
    })
}
