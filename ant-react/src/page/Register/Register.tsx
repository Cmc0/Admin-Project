import {LoginFormPage, ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import LoginBg from "@/asset/img/LoginBg.png";
import Logo from "@/favicon.svg";
import CommonConstant from "@/model/constant/CommonConstant";
import {Form, Tabs} from "antd";
import {LockOutlined, SafetyCertificateOutlined, UserOutlined} from "@ant-design/icons/lib";
import {useEffect, useState} from "react";
import {ToastSuccess} from "../../../util/ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {getAppNav} from "@/App";
import {closeWebSocket} from "../../../util/WebSocketUtil";
import {useAppDispatch} from "@/store";
import {setLoadMenuFlag} from "@/store/userSlice";
import {InDev} from "../../../util/CommonUtil";
import {UserRegByEmailDTO, userRegEmailSendCode} from "@/api/UserRegController";

type RegisterType = 'email' | 'phone';

export default function () {

    const [registerType, setRegisterType] = useState<RegisterType>('email');
    const jwt = localStorage.getItem(LocalStorageKey.JWT)
    const appDispatch = useAppDispatch()

    const [useForm] = Form.useForm<UserRegByEmailDTO>();

    useEffect(() => {
        if (!jwt) {
            appDispatch(setLoadMenuFlag(false)) // 设置：是否加载过菜单为 false
            closeWebSocket() // 关闭 webSocket
        }
    }, [])

    return (
        <div className={"vh100"}>
            <LoginFormPage<UserRegByEmailDTO>
                form={useForm}
                isKeyPressSubmit
                backgroundImageUrl={LoginBg}
                submitter={{searchConfig: {submitText: '注册'}}}
                logo={Logo}
                title={CommonConstant.SYS_NAME}
                subTitle="Will have the most powerful !"
                actions={
                    <div>
                        <a title={"登录已有账号"} onClick={() => getAppNav()(CommonConstant.LOGIN_PATH)}>登录已有账号</a>
                    </div>
                }
                onFinish={async (formData) => {
                    console.log(formData)
                    return true
                }}
            >
                <Tabs activeKey={registerType} onChange={(activeKey) => {
                    if (activeKey === 'phone') {
                        InDev()
                        return
                    }
                    setRegisterType(activeKey as RegisterType)
                }}>
                    <Tabs.TabPane key={'email'} tab={'邮箱注册'}>
                        <>
                            <ProFormText
                                name="email"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <UserOutlined/>,
                                }}
                                placeholder={'邮箱'}
                                rules={[
                                    {
                                        required: true,
                                        message: '请输入邮箱',
                                    },
                                ]}
                            />
                            <ProFormText.Password
                                name="password"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined/>,
                                    allowClear: true
                                }}
                                placeholder={'密码'}
                                rules={[
                                    {
                                        required: true,
                                        message: '请输入密码',
                                    },
                                ]}
                            />
                            <ProFormCaptcha
                                fieldProps={{
                                    size: 'large',
                                    prefix: <SafetyCertificateOutlined/>,
                                }}
                                captchaProps={{
                                    size: 'large',
                                }}
                                placeholder={'请输入验证码'}
                                captchaTextRender={(timing, count) => {
                                    if (timing) {
                                        return `${count} 获取验证码`;
                                    }
                                    return '获取验证码';
                                }}
                                name="code"
                                rules={[
                                    {
                                        required: true,
                                        message: '请输入验证码',
                                    },
                                ]}
                                onGetCaptcha={async () => {
                                    await useForm.validateFields(['email']).then(async res => {
                                        await userRegEmailSendCode({email: res.email!}).then(res => {
                                            ToastSuccess(res.msg)
                                        })
                                    })
                                }}
                            />
                        </>
                    </Tabs.TabPane>
                    <Tabs.TabPane key={'phone'} tab={'手机号注册'}>
                        <>
                        </>
                    </Tabs.TabPane>
                </Tabs>
            </LoginFormPage>
        </div>
    )
}
