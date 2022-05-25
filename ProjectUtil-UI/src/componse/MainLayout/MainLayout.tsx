import ProLayout, {MenuDataItem, PageContainer} from '@ant-design/pro-layout';
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
        <ProLayout
            className={"vh100"}
            title={CommonConstant.SYS_NAME}
            location={{
                pathname: leftMenuList[0].path,
            }}
            menu={{
                request: async () => {
                    return leftMenuList;
                },
            }}
        >
            <PageContainer fixedHeader waterMarkProps={{
                content: CommonConstant.SYS_NAME,
            }}>
                <Outlet/>
            </PageContainer>
        </ProLayout>
    )
}
