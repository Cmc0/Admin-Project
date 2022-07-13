import ProLayout, {PageContainer} from '@ant-design/pro-layout';
import CommonConstant from "@/model/constant/CommonConstant";
import {Outlet} from "react-router-dom";
import React, {Dispatch, SetStateAction, useEffect, useState} from "react";
import {getAppNav} from "@/App";
import {Alert, Avatar, Badge, Button, Dropdown, Menu, Space, Tooltip, Typography} from "antd";
import {
    LogoutOutlined,
    MessageOutlined,
    NotificationOutlined,
    UserOutlined,
    WarningFilled
} from "@ant-design/icons/lib";
import {logout} from "../../../util/UserUtil";
import {InDev} from "../../../util/CommonUtil";
import {execConfirm, ToastError, ToastSuccess} from "../../../util/ToastUtil";
import {useAppDispatch, useAppSelector} from "@/store";
import {connectWebSocket, IWebSocketMessage} from "../../../util/WebSocketUtil";
import {setWebSocketMessage, setWebSocketStatus} from '@/store/commonSlice';
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {setUserSelfBaseInfo, setUserSelfMenuList} from '@/store/userSlice';
import {ListToTree} from "../../../util/TreeUtil";
import {RouterMapKeyList} from "@/router/RouterMap";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {SysMenuDO, sysMenuUserSelfMenuList} from "@/api/SysMenuController";
import {GetWebSocketType, SetWebSocketType, TWebSocketType} from "@/model/constant/LocalStorageKey";
import {sysWebSocketChangeType} from "@/api/SysWebSocketController";
import {sysRequestAllAvg, SysRequestAllAvgVO} from "@/api/SysRequestController";
import {GetAvgType} from "@/page/sysMonitor/Request/Request";
import {MenuDataItem, RouteContext, RouteContextType} from '@ant-design/pro-components';
import {GetPublicDownFileUrl} from "../../../util/FileUtil";
import {userSelfBaseInfo, userSelfLogout} from "@/api/UserSelfController";
import Marquee from 'react-fast-marquee';
import {sysBulletinUserSelfCount} from "@/api/SysBulletinController";

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
    const userSelfMenuList = useAppSelector((state) => state.user.userSelfMenuList)
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
    function doSetElement(userSelfMenuList: SysMenuDO[]) {
        if (element == null) {
            setElement(<MainLayoutElement userSelfMenuList={userSelfMenuList}/>)
        }
    }

    useEffect(() => {

        // 开发时才会用到 ↓
        if (loadMenuFlag) {
            doSetElement(userSelfMenuList)
            goFirstPage(userSelfMenuList)
            return
        }
        // 开发时才会用到 ↑

        sessionStorage.setItem(SessionStorageKey.LOAD_MENU_FLAG, String(false))

        connectWebSocket(doSetSocketMessage, doSetSocketStatus) // 连接 webSocket

        userSelfBaseInfo().then(res => {
            appDispatch(setUserSelfBaseInfo(res.data))
        })

        // 加载菜单
        sysMenuUserSelfMenuList().then(res => {
            if (!res.data || !res.data.length) {
                ToastError('暂未配置菜单，请联系管理员', 5)
                logout()
                return
            }
            appDispatch(setUserSelfMenuList(res.data))
            doSetElement(res.data)
            goFirstPage(res.data)
        })

    }, [])

    return element
}

interface IMainLayoutElement {
    userSelfMenuList: SysMenuDO[]
}

// MainLayout组件页面
function MainLayoutElement(props: IMainLayoutElement) {

    const [pathname, setPathname] = useState<string>('')
    const webSocketStatus = useAppSelector((state) => state.common.webSocketStatus)
    const [webSocketType, setWebSocketType] = useState<TWebSocketType>(GetWebSocketType())
    const [sysRequestAllAvgVO, setSysRequestAllAvgVO] = useState<SysRequestAllAvgVO>({avg: 0, count: 0})
    const userSelfBaseInfo = useAppSelector((state) => state.user.userSelfBaseInfo)

    const [bulletinUserSelfCount, setBulletinUserSelfCount] = useState<number>(0)

    const webSocketMessage = useAppSelector((state) => state.common.webSocketMessage)

    function doSysRequestAllAvg() {
        sysRequestAllAvg({
            headers: {
                hiddenErrorMsg: true,
            },
        }).then(res => {
            setSysRequestAllAvgVO(res.data)
        })
    }

    function doSysBulletinUserSelfCount() {
        sysBulletinUserSelfCount().then(res => {
            setBulletinUserSelfCount(res.data)
        })
    }

    useEffect(() => {
        doSysBulletinUserSelfCount()
    }, [])

    useEffect(() => {
        if (webSocketMessage.code === 6) {
            doSysBulletinUserSelfCount()
        }
    }, [webSocketMessage.code])

    useEffect(() => {
        setPathname(window.location.pathname)
        doSysRequestAllAvg()
        const sysRequestAllAvgInterval = setInterval(doSysRequestAllAvg, 120 * 1000);
        return () => {
            clearInterval(sysRequestAllAvgInterval)
        }
    }, [])

    return <div className={"vh100"}>
        <ProLayout
            title={CommonConstant.SYS_NAME}
            location={{
                pathname
            }}
            menu={{
                request: async () => {
                    const userSelfMenuListTemp: MenuDataItem[] = JSON.parse(JSON.stringify(props.userSelfMenuList));
                    userSelfMenuListTemp.forEach(item => {
                        item.icon = <MyIcon icon={item.icon as string}/>
                        item.hideInMenu = !item.showFlag
                    })
                    return ListToTree(userSelfMenuListTemp, true, 0);
                },
            }}
            fixSiderbar={true}
            fixedHeader={true}
            menuItemRender={(item: MenuDataItem, defaultDom: React.ReactNode) => (
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
                        {defaultDom}
                        {(item.router && !RouterMapKeyList.includes(item.router)) &&
                        <WarningFilled className={"warning2 m-l-4"}/>
                        }
                    </>
                </a>
            )}
            headerContentRender={() => (
                <span className={"hand m-l-9"} title={`全局，接口平均响应耗时，共请求 ${sysRequestAllAvgVO.count}次`}>
                        <Badge
                            status="processing"
                            text={
                                <Typography.Text
                                    strong
                                    type={GetAvgType(sysRequestAllAvgVO.avg!)}>
                                    avg：{sysRequestAllAvgVO.avg}ms
                                </Typography.Text>
                            }/>
                    </span>
            )}
            rightContentRender={() => (
                <RouteContext.Consumer>
                    {(routeContextType: RouteContextType) => {
                        return <Space size={30}>

                            <Space size={routeContextType.isMobile ? 20 : 22}>
                                <Tooltip title="消息">
                                    <a onClick={InDev}>
                                        <Badge count={0} offset={[5, 5]} size={"small"} title={""} className={"hand"}>
                                            <Button type="text" icon={<MessageOutlined/>}/>
                                        </Badge>
                                    </a>
                                </Tooltip>

                                <Tooltip title="公告">
                                    <a onClick={InDev}>
                                        <Badge count={bulletinUserSelfCount} offset={[5, 5]} size={"small"} title={""}
                                               className={"hand"}>
                                            <Button type="text" icon={<NotificationOutlined/>}/>
                                        </Badge>
                                    </a>
                                </Tooltip>
                            </Space>

                            <Dropdown overlay={<Menu items={[
                                {
                                    key: '1',
                                    label: <a onClick={() => doSysWebSocketChangeType('1', setWebSocketType)}>
                                        我在线上
                                    </a>,
                                    icon: <Badge status={"success"}/>
                                },
                                {
                                    key: '2',
                                    label: <a onClick={() => doSysWebSocketChangeType('2', setWebSocketType)}>
                                        隐身
                                    </a>,
                                    icon: <Badge status={"warning"}/>
                                },
                            ]}/>}>
                                <Badge
                                    className={"hand"}
                                    status={webSocketStatus ? (webSocketType === '1' ? 'success' : 'warning') : 'default'}
                                    text={webSocketStatus ? (webSocketType === '1' ? '在线' : '隐身') : '离线'}
                                />
                            </Dropdown>

                            <Dropdown overlayClassName={"body-bc"} overlay={<Menu items={[
                                {
                                    key: '1',
                                    label: <a onClick={() => {
                                        setPathname(CommonConstant.USER_CENTER_PATH)
                                        getAppNav()(CommonConstant.USER_CENTER_PATH)
                                    }}>
                                        个人中心
                                    </a>,
                                    icon: <UserOutlined/>
                                },
                                {
                                    key: '2',
                                    danger: true,
                                    label: <a
                                        onClick={() => {
                                            execConfirm(() => {
                                                return userSelfLogout().then((res) => {
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
                                <div className={"h100 hand"} onClick={() => {
                                    if (!routeContextType.isMobile) {
                                        setPathname(CommonConstant.USER_CENTER_PATH)
                                        getAppNav()(CommonConstant.USER_CENTER_PATH)
                                    }
                                }}>
                                    <Avatar size="small"
                                            src={userSelfBaseInfo.avatarUrl ? GetPublicDownFileUrl(userSelfBaseInfo.avatarUrl) : CommonConstant.RANDOM_AVATAR_URL}/>
                                </div>
                            </Dropdown>
                        </Space>
                    }}
                </RouteContext.Consumer>
            )}
        >
            <PageContainer>
                <Alert
                    banner
                    closable
                    message={
                        <Marquee pauseOnHover gradient={false}>
                            I can be a React component, multiple React components, or just some text.
                        </Marquee>
                    }
                />
                <Outlet/>
            </PageContainer>
        </ProLayout>
    </div>
}

function doSysWebSocketChangeType(value: TWebSocketType, setWebSocketType: Dispatch<SetStateAction<TWebSocketType>>) {

    const webSocketId = sessionStorage.getItem(SessionStorageKey.WEB_SOCKET_ID);

    if (!webSocketId) {
        ToastError("切换状态失败，请刷新页面")
        return
    }

    sysWebSocketChangeType({id: Number(webSocketId), value: Number(value)}).then(res => {
        ToastSuccess(res.msg)
        SetWebSocketType(value)
        setWebSocketType(value)
    })
}
