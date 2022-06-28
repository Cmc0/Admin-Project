import {LoginFormPage, ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import LoginBg from "@/asset/img/LoginBg.png";
import Logo from "@/favicon.svg";
import CommonConstant from "@/model/constant/CommonConstant";
import {Form, Tabs} from "antd";
import {LockOutlined, SafetyCertificateOutlined, UserOutlined} from "@ant-design/icons/lib";
import {useEffect, useState} from "react";
import {ToastSuccess} from "../../../util/ToastUtil";
import {getAppNav} from "@/App";
import {useAppSelector} from "@/store";
import {InDev} from "../../../util/CommonUtil";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../util/RsaUtil";
import RegisterTest from "@/page/Register/RegisterTest";
import {UserRegisterByEmailDTO, userRegisterEmail, userRegisterEmailSendCode} from "@/api/UserRegisterController";
import {UseEffectLogin} from "@/page/Login/Login";
import ValidatorUtil from "../../../util/ValidatorUtil";

type RegisterType = 'email' | 'phone';

export default function () {

    const [registerType, setRegisterType] = useState<RegisterType>('email');
    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)
    const [useForm] = Form.useForm<UserRegisterByEmailDTO>();

    useEffect(() => {
        window.PageTest = function () {
            RegisterTest(useForm)
        }
    }, [])

    UseEffectLogin()

    return (
        <div className={"vh100"}>
            <LoginFormPage<UserRegisterByEmailDTO>
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
                onFinish={async (form) => {

                    const formTemp = {...form}

                    const date = new Date()
                    formTemp.origPassword = RSAEncryptPro(formTemp.password, rsaPublicKey, date)
                    formTemp.password = PasswordRSAEncrypt(formTemp.password, rsaPublicKey, date)

                    await userRegisterEmail(formTemp).then(res => {
                        ToastSuccess(res.msg)
                        getAppNav()(CommonConstant.LOGIN_PATH)
                    })

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
                                    allowClear: true,
                                }}
                                placeholder={'邮箱'}
                                rules={[{validator: ValidatorUtil.emailValidate}]}
                            />
                            <ProFormText.Password
                                name="password"
                                fieldProps={{
                                    size: 'large',
                                    prefix: <LockOutlined/>,
                                    allowClear: true,
                                }}
                                placeholder={'密码'}
                                rules={[{validator: ValidatorUtil.passwordValidate}]}
                            />
                            <ProFormCaptcha
                                fieldProps={{
                                    size: 'large',
                                    maxLength: 6,
                                    allowClear: true,
                                    prefix: <SafetyCertificateOutlined/>,
                                }}
                                captchaProps={{
                                    size: 'large',
                                }}
                                placeholder={'请输入验证码'}
                                name="code"
                                rules={[{validator: ValidatorUtil.codeValidate}]}
                                onGetCaptcha={async () => {
                                    await useForm.validateFields(['email']).then(async res => {
                                        await userRegisterEmailSendCode({email: res.email!}).then(res => {
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
