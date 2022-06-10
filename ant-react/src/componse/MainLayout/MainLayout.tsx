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

export default function () {

    const [pathname, setPathname] = useState<string>()

    useEffect(() => {
        setPathname(window.location.pathname)
    }, [])

    if (window.location.pathname === CommonConstant.MAIN_PATH) {
        if (MainLayoutRouterPathList[0]) {
            return <Navigate to={MainLayoutRouterPathList[0]}/>
        }
    }

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
                                    logout('登出成功')
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
