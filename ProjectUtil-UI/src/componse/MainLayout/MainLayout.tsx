import ProLayout, {MenuDataItem, PageContainer, SettingDrawer} from '@ant-design/pro-layout';
import {Avatar} from "antd";
import {UserOutlined,} from '@ant-design/icons';
import CommonConstant from "@/model/constant/CommonConstant";
import {Outlet} from "react-router-dom";

const leftMenuList: MenuDataItem[] = [
    {
        path: '/main/dbManage123',
        name: '数据库管理',
    }
]

export default function () {

    return (
        <div className={"vh100"} id="pro-layout">
            <ProLayout
                title={CommonConstant.SYS_NAME}
                location={{
                    pathname: leftMenuList[0].path,
                }}
                menu={{
                    request: async () => {
                        return leftMenuList;
                    },
                }}
                rightContentRender={() => (
                    <div>
                        <Avatar shape="square" size="small" icon={<UserOutlined/>}/>
                    </div>
                )}>
                <PageContainer fixedHeader waterMarkProps={{
                    content: CommonConstant.SYS_NAME,
                }}>
                    <Outlet/>
                </PageContainer>
            </ProLayout>

            <SettingDrawer
                enableDarkTheme
                getContainer={() => document.getElementById('pro-layout')}
                disableUrlParams={false}
            />
        </div>
    )
}
