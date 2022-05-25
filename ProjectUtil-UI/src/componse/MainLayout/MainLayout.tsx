import {Layout, Menu, PageHeader} from "antd";
import {Outlet} from "react-router-dom";
import {ItemType} from "antd/lib/menu/hooks/useItems";
import {useState} from "react";

const leftMenuList: ItemType[] = [
    {label: '数据库管理', key: 'dbManage'},
];

const selectedKeys = ['dbManage']

export default function () {
    const [leftMenuCollapsed, setLeftMenuCollapsed] = useState(false);

    return (
        <Layout className={"vwh100 overflow bg"}>
            <Layout.Header/>
            <Layout>
                <Layout.Sider theme={"light"} collapsible collapsed={leftMenuCollapsed} onCollapse={(collapsed) => {
                    setLeftMenuCollapsed(collapsed)
                }}>
                    <Menu items={leftMenuList} mode={"inline"} selectedKeys={selectedKeys}/>
                </Layout.Sider>
                <Layout.Content className={"overflow h100"}>
                    <PageHeader title="数据库管理" ghost={false}/>
                    <Outlet/>
                </Layout.Content>
            </Layout>
        </Layout>
    )
}
