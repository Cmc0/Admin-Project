import {SettingOutlined, UserOutlined} from "@ant-design/icons/lib";
import UserCenterBaseInfo from "@/page/user/Center/UserCenterBaseInfo";
import UserCenterSetting from "@/page/user/Center/UserCenterSetting";
import {Card, Tabs} from "antd";
import {RouteContext, RouteContextType} from "@ant-design/pro-components";

export default function () {
    return (
        <RouteContext.Consumer>
            {(value: RouteContextType) => {
                return <Card>
                    <Tabs tabPosition={value.isMobile ? 'top' : 'left'}>
                        <Tabs.TabPane key={'1'} tab={
                            <span><UserOutlined/> 个人资料</span>
                        }>
                            <UserCenterBaseInfo/>
                        </Tabs.TabPane>
                        <Tabs.TabPane key={'2'} tab={
                            <span><SettingOutlined/> 账号设置</span>
                        }>
                            <UserCenterSetting/>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>
            }}
        </RouteContext.Consumer>
    )
}
