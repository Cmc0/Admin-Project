import {SettingOutlined, UserOutlined} from "@ant-design/icons/lib";
import UserCenterBaseInfo from "@/page/user/Center/UserCenterBaseInfo";
import UserCenterSetting from "@/page/user/Center/UserCenterSetting";
import {Tabs} from "antd";

export default function () {
    return <Tabs tabPosition='left'>
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

}
