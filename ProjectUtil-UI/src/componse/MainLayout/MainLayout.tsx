import {Layout} from "antd";

export default function MainLayout() {
    return (
        <Layout>
            <Layout.Header>header</Layout.Header>
            <Layout>
                <Layout.Sider>left sidebar</Layout.Sider>
                <Layout.Content>main content</Layout.Content>
                <Layout.Sider>right sidebar</Layout.Sider>
            </Layout>
            <Layout.Footer>footer</Layout.Footer>
        </Layout>
    )
}
