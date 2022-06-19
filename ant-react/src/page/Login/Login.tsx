import {LoginFormPage, ProFormCheckbox, ProFormText} from "@ant-design/pro-components";
import LoginBg from "@/asset/img/LoginBg.png";
import Logo from "@/favicon.svg";
import CommonConstant from "@/model/constant/CommonConstant";
import {Divider, Space, Tabs, Typography} from "antd";
import {LockOutlined, QqOutlined, UserOutlined, WechatOutlined} from "@ant-design/icons/lib";
import {useEffect, useState} from "react";
import {UserLoginByPasswordDTO, userLoginPassword} from "@/api/UserLoginController";
import {ToastSuccess} from "../../../util/ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {PasswordRSAEncrypt} from "../../../util/RsaUtil";
import {Navigate} from "react-router-dom";
import {getAppNav} from "@/App";
import {closeWebSocket} from "../../../util/WebSocketUtil";
import {useAppDispatch} from "@/store";
import {setLoadMenuFlag} from "@/store/userSlice";
import {InDev} from "../../../util/CommonUtil";

type LoginType = 'password' | 'phone';

export default function () {

    const [loginType, setLoginType] = useState<LoginType>('password');
    const jwt = localStorage.getItem(LocalStorageKey.JWT)
    const appDispatch = useAppDispatch()

    useEffect(() => {
        if (!jwt) {
            appDispatch(setLoadMenuFlag(false)) // 设置：是否加载过菜单为 false
            closeWebSocket() // 关闭 webSocket
        }
    }, [])

    if (jwt) {
        return <Navigate to={"/"}/>
    }

    return (
        <div className={"vh100"}>
            <LoginFormPage<UserLoginByPasswordDTO>
                isKeyPressSubmit
                backgroundImageUrl={LoginBg}
                logo={Logo}
                title={CommonConstant.SYS_NAME}
                subTitle="Will have the most powerful !"
                actions={
                    <div>
                        <div>或者 <a title={"注册"} onClick={() => getAppNav()(CommonConstant.REGISTER_PATH)}>注册</a></div>
                        <Divider plain>
                            <Typography.Text type="secondary">其他登录方式</Typography.Text>
                        </Divider>
                        <Space className={"flex-center f-18"} size={24}>
                            <div className={"flex-center border-a b-r-b-50 w-40 h-40 hand"} title={"QQ登录"}
                                 onClick={InDev}>
                                <QqOutlined className={"blue1"}/>
                            </div>
                            <div className={"flex-center border-a b-r-b-50 w-40 h-40 hand"} title={"微信登录"}
                                 onClick={InDev}>
                                <WechatOutlined className={"green2"}/>
                            </div>
                        </Space>
                    </div>
                }
                onFinish={async (formData) => {
                    await userLoginPassword({
                        ...formData,
                        password: PasswordRSAEncrypt(formData.password)!
                    }).then(res => {
                        localStorage.clear()
                        sessionStorage.clear()
                        ToastSuccess('欢迎回来~')
                        localStorage.setItem(LocalStorageKey.JWT, res.data)
                        getAppNav()(CommonConstant.MAIN_PATH)
                    })

                    return true
                }}
            >
                <Tabs activeKey={loginType} onChange={(activeKey) => {
                    if (activeKey === 'phone') {
                        InDev()
                        return
                    }
                    setLoginType(activeKey as LoginType)
                }}>
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
                                    prefix: <LockOutlined/>,
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
                    <Tabs.TabPane key={'phone'} tab={'手机号登录'}>
                        <>
                        </>
                    </Tabs.TabPane>
                </Tabs>
                <div className={"flex jc-sb"}>
                    <ProFormCheckbox name="rememberMe">
                                    <span title={"记住我"}>
                                        记住我
                                        <Typography.Text type="secondary"> 7天免登录</Typography.Text>
                                    </span>
                    </ProFormCheckbox>
                    <a title={"忘记密码"} onClick={InDev}>
                        忘记密码
                    </a>
                </div>
            </LoginFormPage>
        </div>
    )
}
