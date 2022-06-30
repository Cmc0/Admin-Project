import {USER_CENTER_KEY_TWO} from "./Center";
import {List} from "antd";
import React, {ReactNode} from "react";
import {useAppSelector} from "@/store";
import {ModalForm, ProFormText} from "@ant-design/pro-components";
import {SysUserUpdatePasswordDTO} from "@/api/SysUserController";
import CommonConstant from "@/model/constant/CommonConstant";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";
import {ValidatorUtil} from "../../../../util/ValidatorUtil";

interface IUserCenterSetting {
    title: string
    description?: string
    actions: ReactNode[];
}

const UserCenterUpdatePasswordTitle = "修改密码"

export function UserCenterUpdatePasswordModalForm() {

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)

    return <ModalForm<SysUserUpdatePasswordDTO>
        modalProps={{
            maskClosable: false
        }}
        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserCenterUpdatePasswordTitle}
        trigger={<a>{UserCenterUpdatePasswordTitle}</a>}
        onFinish={async (form) => {
            const formTemp = {...form}
            if (formTemp.newPassword) {
                const date = new Date()
                formTemp.newOrigPassword = RSAEncryptPro(formTemp.newPassword, rsaPublicKey, date)
                formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword, rsaPublicKey, date)
            }
            return true
        }}
    >
        <ProFormText label="新密码" name="newPassword" required
                     rules={[{validator: ValidatorUtil.passwordValidate}]}/>
    </ModalForm>
}

export default function () {
    return (
        <List<IUserCenterSetting>
            header={USER_CENTER_KEY_TWO}
            rowKey={"title"}
            dataSource={[
                {
                    title: '密码',
                    actions: [
                        <UserCenterUpdatePasswordModalForm key="1"/>,
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
