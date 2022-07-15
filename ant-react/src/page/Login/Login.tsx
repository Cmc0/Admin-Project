import {LoginFormPage, ModalForm, ProFormCaptcha, ProFormCheckbox, ProFormText} from "@ant-design/pro-components";
import LoginBg from "@/asset/img/LoginBg.png";
import Logo from "/vite.svg";
import CommonConstant from "@/model/constant/CommonConstant";
import {Divider, Form, Space, Tabs, Typography} from "antd";
import {LockOutlined, QqOutlined, UserOutlined, WechatOutlined} from "@ant-design/icons/lib";
import React, {useEffect, useState} from "react";
import {UserLoginByPasswordDTO, userLoginPassword} from "@/api/UserLoginController";
import {ToastSuccess} from "../../../util/ToastUtil";
import LocalStorageKey, {SetWebSocketType} from "@/model/constant/LocalStorageKey";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../util/RsaUtil";
import {Navigate} from "react-router-dom";
import {getAppNav} from "@/App";
import {closeWebSocket} from "../../../util/WebSocketUtil";
import {useAppDispatch, useAppSelector} from "@/store";
import {setLoadMenuFlag} from "@/store/userSlice";
import {InDev} from "../../../util/CommonUtil";
import {ValidatorUtil} from "../../../util/ValidatorUtil";
import {
    userForgotPassword,
    UserForgotPasswordDTO,
    userForgotPasswordSendEmailCode
} from "@/api/UserForgotPasswordController";

type LoginType = 'password' | 'phone';

export function UseEffectLogin() {
    const appDispatch = useAppDispatch()
    useEffect(() => {
        if (!localStorage.getItem(LocalStorageKey.JWT)) {
            appDispatch(setLoadMenuFlag(false)) // 设置：是否加载过菜单为 false
            closeWebSocket() // 关闭 webSocket
        }
    }, [])
}

export default function () {

    const [loginType, setLoginType] = useState<LoginType>('password');
    const [useForm] = Form.useForm<UserLoginByPasswordDTO>();

    UseEffectLogin()

    if (localStorage.getItem(LocalStorageKey.JWT)) {
        return <Navigate to={"/"}/>
    }

    return (
        <div className={"vh100"}>
            <LoginFormPage<UserLoginByPasswordDTO>
                form={useForm}
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
                onFinish={async (form) => {
                    await userLoginPassword({
                        ...form,
                        password: PasswordRSAEncrypt(form.password)!
                    }).then(res => {
                        localStorage.clear()
                        sessionStorage.clear()
                        SetWebSocketType()
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
                                extra={"测试账号：123@qq.com 测试密码：demoDemo123"}
                                name="account"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <UserOutlined/>,
                                    autoComplete: "new-password",
                                }}
                                placeholder={'邮箱'}
                                rules={[
                                    import.meta.env.DEV ?
                                        {
                                            required: true,
                                            message: "请输入邮箱",
                                        } : {
                                            validator: ValidatorUtil.emailValidate
                                        }
                                ]}
                            />
                            <ProFormText.Password
                                name="password"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined/>,
                                    allowClear: true,
                                    autoComplete: "new-password",
                                    visibilityToggle: false,
                                }}
                                placeholder={'密码'}
                                rules={[
                                    {
                                        required: true,
                                        message: "请输入密码",
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
                    <UserForgotPasswordModalForm/>
                </div>
            </LoginFormPage>
        </div>
    )
}

const userForgotPasswordModalTitle = "忘记密码"

function UserForgotPasswordModalForm() {

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)
    const [useForm] = Form.useForm<UserForgotPasswordDTO>();

    return <ModalForm<UserForgotPasswordDTO>
        modalProps={{
            maskClosable: false
        }}
        form={useForm}
        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={userForgotPasswordModalTitle}
        trigger={<a>{userForgotPasswordModalTitle}</a>}
        onFinish={async (form) => {
            const formTemp = {...form}
            if (formTemp.newPassword) {
                const date = new Date()
                formTemp.newOrigPassword = RSAEncryptPro(formTemp.newPassword, rsaPublicKey, date)
                formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword, rsaPublicKey, date)
            }
            await userForgotPassword(formTemp).then(res => {
                ToastSuccess(res.msg)
            })
            return true
        }}
    >
        <ProFormText
            name="account"
            fieldProps={{
                allowClear: true,
                autoComplete: "new-password",
            }}
            required
            label="邮箱"
            placeholder={'请输入邮箱'}
            rules={[{validator: ValidatorUtil.emailValidate}]}
        />
        <ProFormCaptcha
            fieldProps={{
                maxLength: 6,
                allowClear: true,
                autoComplete: "new-password",
            }}
            required
            label="验证码"
            placeholder={'请输入验证码'}
            name="code"
            rules={[{validator: ValidatorUtil.codeValidate}]}
            onGetCaptcha={async () => {
                await useForm.validateFields(['account']).then(async res => {
                    await userForgotPasswordSendEmailCode({email: res.account!}).then(res => {
                        ToastSuccess(res.msg)
                    })
                })
            }}
        />
        <ProFormText
            label="新密码"
            placeholder={'请输入新密码'}
            name="newPassword"
            required
            fieldProps={{
                allowClear: true,
                autoComplete: "new-password",
            }}
            rules={[{validator: ValidatorUtil.passwordValidate}]}/>
    </ModalForm>
}
