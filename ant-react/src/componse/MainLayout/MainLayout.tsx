import ProLayout, {PageContainer} from '@ant-design/pro-layout';
import CommonConstant from "@/model/constant/CommonConstant";
import {Navigate, Outlet} from "react-router-dom";
import MainLayoutRouterList, {IMainLayoutRouterList, MainLayoutRouterPathList} from "@/router/MainLayoutRouterList";
import React, {useEffect, useState} from "react";
import {getAppNav} from "@/App";

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
        >
            <PageContainer>
                <Outlet/>
            </PageContainer>
        </ProLayout>
    )
}
