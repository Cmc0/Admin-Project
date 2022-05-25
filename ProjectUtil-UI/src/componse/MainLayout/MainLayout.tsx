import {Layout, Menu, PageHeader} from "antd";
import {Outlet} from "react-router-dom";
import {ItemType} from "antd/lib/menu/hooks/useItems";
import {useState} from "react";

const leftMenuList: ItemType[] = [
    {label: '数据库管理', key: 'dbManage'},
];

const selectedKeys = ['dbManage']

export default function () {
    const [collapsed, setCollapsed] = useState(false);

    return (
        <Layout className={"vwh100 overflow bg"}>
            <Layout.Header/>
            <Layout>
                <Layout.Sider theme={"light"} collapsible collapsed={collapsed} onCollapse={(collapsed) => {
                    setCollapsed(collapsed)
                }}>
                    <Menu items={leftMenuList} mode={"inline"} selectedKeys={selectedKeys}/>
                </Layout.Sider>
                <Layout.Content className={"h100 flex-c"}>
                    <PageHeader title="数据库管理" ghost={false}/>
                    <div className={'flex-1 overflow h100'}>
                        <Outlet/>
                    </div>
                </Layout.Content>
            </Layout>
        </Layout>
    )
}
