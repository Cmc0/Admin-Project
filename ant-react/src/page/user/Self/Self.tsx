import {SettingOutlined, UserOutlined} from "@ant-design/icons/lib";
import {Card, Tabs} from "antd";
import {RouteContext, RouteContextType} from "@ant-design/pro-components";
import UserSelfBaseInfo from "@/page/user/Self/UserSelfBaseInfo";
import UserSelfSetting from "@/page/user/Self/UserSelfSetting";

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
                            <UserSelfBaseInfo/>
                        </Tabs.TabPane>
                        <Tabs.TabPane key={'2'} tab={
                            <span><SettingOutlined/>{USER_CENTER_KEY_TWO}</span>
                        }>
                            <UserSelfSetting/>
                        </Tabs.TabPane>
                    </Tabs>
                </Card>
            }}
        </RouteContext.Consumer>
    )
}
