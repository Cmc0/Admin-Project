import {USER_CENTER_KEY_TWO} from "./Self";
import {Form, List} from "antd";
import React, {ReactNode} from "react";
import {useAppSelector} from "@/store";
import {ModalForm, ProFormCaptcha, ProFormText} from "@ant-design/pro-components";
import {SysUserUpdatePasswordDTO} from "@/api/SysUserController";
import CommonConstant from "@/model/constant/CommonConstant";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";
import {ValidatorUtil} from "../../../../util/ValidatorUtil";
import {ToastSuccess} from "../../../../util/ToastUtil";
import {
    userSelfUpdatePassword,
    UserSelfUpdatePasswordDTO,
    userSelfUpdatePasswordSendEmailCode
} from "@/api/UserSelfController";
import {logout} from "../../../../util/UserUtil";

interface IUserSelfSetting {
    title: string
    description?: string
    actions: ReactNode[];
}

const UserSelfUpdatePasswordTitle = "修改密码"

export function UserSelfUpdatePasswordModalForm() {

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)
    const [useForm] = Form.useForm<UserSelfUpdatePasswordDTO>();

    return <ModalForm<SysUserUpdatePasswordDTO>
        modalProps={{
            maskClosable: false
        }}
        form={useForm}
        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfUpdatePasswordTitle}
        trigger={<a>{UserSelfUpdatePasswordTitle}</a>}
        onFinish={async (form) => {
            const formTemp = {...form}
            if (formTemp.newPassword) {
                const date = new Date()
                formTemp.newOrigPassword = RSAEncryptPro(formTemp.newPassword, rsaPublicKey, date)
                formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword, rsaPublicKey, date)
            }
            await userSelfUpdatePassword(formTemp).then(res => {
                ToastSuccess(res.msg)
                logout()
            })
            return true
        }}
    >
        <ProFormCaptcha
            fieldProps={{
                maxLength: 6,
                allowClear: true,
            }}
            required
            label="验证码"
            placeholder={'请输入验证码'}
            name="code"
            rules={[{validator: ValidatorUtil.codeValidate}]}
            onGetCaptcha={async () => {
                await userSelfUpdatePasswordSendEmailCode().then(res => {
                    ToastSuccess(res.msg)
                })
            }}
        />
        <ProFormText label="新密码" name="newPassword" required
                     rules={[{validator: ValidatorUtil.passwordValidate}]}/>
    </ModalForm>
}

export default function () {
    return (
        <List<IUserSelfSetting>
            header={USER_CENTER_KEY_TWO}
            rowKey={"title"}
            dataSource={[
                {
                    title: '密码',
                    actions: [
                        <UserSelfUpdatePasswordModalForm key="1"/>,
                    ]
                },
                {
                    title: '邮箱',
                    description: '1*********@qq.com',
                    actions: [
                        <a key="1" onClick={() => {

                        }}>
                            修改邮箱
                        </a>
                    ]
                },
                {
                    title: '刷新令牌',
                    description: '刷新之后，执行任意操作，都会要求重新登录，用于：不修改密码，退出所有登录',
                    actions: [
                        <a key="1" onClick={() => {

                        }}>
                            执行刷新
                        </a>
                    ]
                },
                {
                    title: '登录记录',
                    actions: [
                        <a key="1" onClick={() => {

                        }}>
                            查看记录
                        </a>
                    ]
                },
                {
                    title: '账号注销',
                    actions: [
                        <a className={"red3"} key="1" onClick={() => {

                        }}>
                            立即注销
                        </a>
                    ]
                },
            ]}
            renderItem={item => (
                <List.Item actions={item.actions}>
                    <List.Item.Meta
                        title={item.title}
                        description={item.description}
                    />
                </List.Item>
            )}
        />
    )
}
