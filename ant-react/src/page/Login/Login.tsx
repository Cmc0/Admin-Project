import {LoginFormPage, ProFormCheckbox, ProFormText} from "@ant-design/pro-components";
import LoginBg from "@/asset/img/LoginBg.png"
import CommonConstant from "@/model/constant/CommonConstant";
import {Tabs} from "antd";
import {LockOutlined, UserOutlined} from "@ant-design/icons/lib";
import {useState} from "react";
import {userLoginPassword, UserLoginPasswordDTO} from "@/api/UserLoginController";
import {ToastSuccess} from "../../../util/ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {PasswordRSAEncrypt} from "../../../util/RsaUtil";
import {Navigate} from "react-router-dom";
import {getAppNav} from "@/App";

type LoginType = 'password';

export default function () {

    const [loginType, setLoginType] = useState<LoginType>('password');

    if (localStorage.getItem(LocalStorageKey.JWT)) {
        return <Navigate to={"/"}/>
    }

    return (
        <div className={"vh100"}>
            <LoginFormPage<UserLoginPasswordDTO>
                isKeyPressSubmit
                backgroundImageUrl={LoginBg}
                logo={"/src/favicon.svg"}
                title={CommonConstant.SYS_NAME}
                subTitle="Will have the most powerful !"
                onFinish={async (formData) => {

                    userLoginPassword({...formData, password: PasswordRSAEncrypt(formData.password)!}).then(res => {
                        localStorage.clear()
                        sessionStorage.clear()
                        ToastSuccess('欢迎回来~')
                        localStorage.setItem(LocalStorageKey.JWT, res.data)
                        getAppNav()(CommonConstant.MAIN_PATH)
                    })
                }}
            >
                <Tabs activeKey={loginType} onChange={(activeKey) => setLoginType(activeKey as LoginType)}>
                    <Tabs.TabPane key={'password'} tab={'账号密码登录'}>
                        <>
                            <ProFormText
                                name="account"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <UserOutlined/>,
                                }}
                                placeholder={'邮箱'}
                                rules={[
                                    {
                                        required: true,
                                    },
                                ]}
                            />
                            <ProFormText.Password
                                name="password"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined className={'prefixIcon'}/>,
                                }}
                                placeholder={'密码'}
                                rules={[
                                    {
                                        required: true,
                                    },
                                ]}
                            />
                        </>
                    </Tabs.TabPane>
                </Tabs>
                <div className={"flex jc-sb"}>
                    <ProFormCheckbox name="rememberMe">
                        自动登录
                    </ProFormCheckbox>
                    <a>
                        忘记密码
                    </a>
                </div>
            </LoginFormPage>
        </div>
    )
}
