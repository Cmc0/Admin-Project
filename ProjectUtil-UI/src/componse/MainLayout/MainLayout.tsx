import ProLayout, {PageContainer} from '@ant-design/pro-layout';
import CommonConstant from "@/model/constant/CommonConstant";
import {Outlet} from "react-router-dom";
import MainLayoutRouterList, {IMainLayoutRouterList} from "@/router/MainLayoutRouterList";
import React, {useState} from "react";
import {getAppNav} from "@/App";

export default function () {

    const [pathname, setPathname] = useState(MainLayoutRouterList[0].path);

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
            menuItemRender={(item: IMainLayoutRouterList, dom: React.ReactNode) => (
                <a
                    onClick={() => {
                        if (item.path && item.element) {
                            getAppNav()(item.path)
                            setPathname(item.path)
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
