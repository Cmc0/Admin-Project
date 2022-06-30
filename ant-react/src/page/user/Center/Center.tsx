import {SettingOutlined, UserOutlined} from "@ant-design/icons/lib";
import UserCenterBaseInfo from "@/page/user/Center/UserCenterBaseInfo";
import UserCenterSetting from "@/page/user/Center/UserCenterSetting";
import {Card, Tabs} from "antd";
import {RouteContext, RouteContextType} from "@ant-design/pro-components";

export const USER_CENTER_KEY_ONE = "个人资料"
export const USER_CENTER_KEY_TWO = "账号设置"

export default function () {
    return (
        <RouteContext.Consumer>
            {(value: RouteContextType) => {
                return <Card>
                    <Tabs tabPosition={value.isMobile ? 'top' : 'left'}>
                        <Tabs.TabPane key={'1'} tab={
                            <span><UserOutlined/>{USER_CENTER_KEY_ONE}</span>
                        }>
                            <UserCenterBaseInfo/>
                        </Tabs.TabPane>
                        <Tabs.TabPane key={'2'} tab={
                            <span><SettingOutlined/>{USER_CENTER_KEY_TWO}</span>
                        }>
                            <UserCenterSetting/>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>
            }}
        </RouteContext.Consumer>
    )
}
