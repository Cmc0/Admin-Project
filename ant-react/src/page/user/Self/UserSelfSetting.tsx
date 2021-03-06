import {USER_CENTER_KEY_TWO} from "./Self";
import {Button, List, Modal} from "antd";
import React, {ReactNode, useRef, useState} from "react";
import {useAppSelector} from "@/store";
import {
    ModalForm,
    ProFormCaptcha,
    ProFormInstance,
    ProFormText,
    ProTable,
    RouteContext,
    RouteContextType,
    StepsForm
} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";
import {ValidatorUtil} from "../../../../util/ValidatorUtil";
import {execConfirm, ToastError, ToastSuccess} from "../../../../util/ToastUtil";
import {
    userSelfDelete,
    userSelfDeleteSendEmailCode,
    userSelfRefreshJwtSecretSuf,
    userSelfUpdateEmail,
    UserSelfUpdateEmailDTO,
    userSelfUpdateEmailSendEmailCode,
    userSelfUpdateEmailSendEmailCodeCodeToKey,
    userSelfUpdatePassword,
    UserSelfUpdatePasswordDTO,
    userSelfUpdatePasswordSendEmailCode
} from "@/api/UserSelfController";
import {logout} from "../../../../util/UserUtil";
import MyCodeToKeyDTO from "@/model/dto/MyCodeToKeyDTO";
import {userRegisterEmailSendCode} from "@/api/UserRegisterController";
import {ApiResultVO} from "../../../../util/HttpUtil";
import {SysMenuDO, SysMenuPageDTO} from "@/api/SysMenuController";
import {handlerRegion} from "../../../../util/StrUtil";
import {sysRequestSelfLoginRecord} from "@/api/SysRequestController";
import {RequestGetDictList} from "../../../../util/DictUtil";

interface IUserSelfSetting {
    title: string
    description?: string
    actions: ReactNode[];
}

const UserSelfUpdatePasswordTitle = "修改密码"

export function UserSelfUpdatePasswordModalForm() {

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)

    return <ModalForm<UserSelfUpdatePasswordDTO>
        modalProps={{
            maskClosable: false
        }}
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
                autoComplete: "new-password",
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

const UserSelfUpdateEmailTitle = "修改邮箱"
const UserSelfUpdateEmailStepOneName = "身份验证"

export function UserSelfUpdateEmailModalForm() {

    const keyRef = useRef<string>('');
    const [submitFlag, setSubmitFlag] = useState<boolean>(false);
    const formRef = useRef<ProFormInstance<UserSelfUpdateEmailDTO>>();
    const [visible, setVisible] = useState(false);

    return <>
        <a onClick={() => {
            setVisible(true)
        }}>{UserSelfUpdateEmailTitle}</a>
        <StepsForm<UserSelfUpdateEmailDTO>
            stepsFormRender={(dom, submitter) => {
                return (
                    <Modal
                        title={UserSelfUpdateEmailTitle}
                        onCancel={() => setVisible(false)}
                        visible={visible}
                        footer={submitter}
                        maskClosable={false}
                    >
                        {dom}
                    </Modal>
                );
            }}
            formRef={formRef}
            submitter={{
                render: (props) => {
                    if (props.step === 0) {
                        return (
                            <Button loading={submitFlag} type="primary" onClick={() => props.onSubmit?.()}>
                                {UserSelfUpdateEmailStepOneName}
                            </Button>
                        );
                    }
                    return <Button loading={submitFlag} type="primary" onClick={() => props.onSubmit?.()}>
                        {UserSelfUpdateEmailTitle}
                    </Button>
                },
            }}
            onFinish={async (form) => {
                if (!keyRef.current) {
                    ToastError(`非法操作，请重新进行${UserSelfUpdateEmailStepOneName}`)
                    return true
                }
                setSubmitFlag(true)
                let nextFlag = false
                await userSelfUpdateEmail({code: form.code, email: form.email, key: keyRef.current}).then(res => {
                    ToastSuccess(res.msg)
                    nextFlag = true
                    setSubmitFlag(false)
                    setVisible(false)
                    logout()
                }).catch((err: ApiResultVO) => {
                    if (err.code === 100191) {
                        nextFlag = true // 重置表单，并返回第一个步骤
                    }
                    setSubmitFlag(false)
                })
                return nextFlag
            }}
        >
            <StepsForm.StepForm<MyCodeToKeyDTO>
                title={UserSelfUpdateEmailStepOneName}
                onFinish={async (form) => {
                    setSubmitFlag(true)
                    let nextFlag = false
                    await userSelfUpdateEmailSendEmailCodeCodeToKey({code: form.code}).then(res => {
                        keyRef.current = res.data
                        ToastSuccess(res.msg)
                        nextFlag = true
                        setSubmitFlag(false)
                    }).catch(() => {
                        setSubmitFlag(false)
                    })
                    return nextFlag
                }}>
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
                        await userSelfUpdateEmailSendEmailCode().then(res => {
                            ToastSuccess(res.msg)
                        })
                    }}
                />
            </StepsForm.StepForm>
            <StepsForm.StepForm
                title={UserSelfUpdateEmailTitle}
            >
                <ProFormText
                    name="email"
                    fieldProps={{
                        allowClear: true,
                    }}
                    required
                    label="新邮箱"
                    placeholder={'请输入新邮箱'}
                    rules={[{validator: ValidatorUtil.emailValidate}]}
                />
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
                        await formRef.current?.validateFields(['email']).then(async res => {
                            await userRegisterEmailSendCode({email: res.email!}).then(res => {
                                ToastSuccess(res.msg)
                            })
                        })
                    }}
                />
            </StepsForm.StepForm>
        </StepsForm>
    </>
}

export default function () {

    const userSelfBaseInfo = useAppSelector((state) => state.user.userSelfBaseInfo)

    return (
        <List<IUserSelfSetting>
            header={USER_CENTER_KEY_TWO}
            rowKey={"title"}
            dataSource={[
                {
                    title: '密码',
                    actions: [
                        <UserSelfUpdatePasswordModalForm key={"1"}/>,
                    ]
                },
                {
                    title: '邮箱',
                    description: userSelfBaseInfo.email,
                    actions: [
                        <UserSelfUpdateEmailModalForm key={"1"}/>
                    ]
                },
                {
                    title: '刷新令牌',
                    description: '刷新之后，执行任意操作，都会要求重新登录，用于：不修改密码，退出所有登录',
                    actions: [
                        <a key="1" onClick={() => {
                            execConfirm(() => {
                                return userSelfRefreshJwtSecretSuf().then(res => {
                                    ToastSuccess(res.msg)
                                })
                            }, undefined, '确定执行【刷新令牌】操作吗？')
                        }}>
                            执行刷新
                        </a>
                    ]
                },
                {
                    title: RequestSelfLoginRecordModalTitle,
                    actions: [
                        <RequestSelfLoginRecordModal key={"1"}/>
                    ]
                },
                {
                    title: UserSelfDeleteModalTitle,
                    actions: [
                        <UserSelfDeleteModalForm key={"1"}/>
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

const UserSelfDeleteModalTitle = "账号注销"
const UserSelfDeleteModalTargetName = "立即注销"

export function UserSelfDeleteModalForm() {

    return <ModalForm<UserSelfUpdatePasswordDTO>
        modalProps={{
            maskClosable: false
        }}
        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={UserSelfDeleteModalTitle}
        trigger={<a className={"red3"}>{UserSelfDeleteModalTargetName}</a>}
        onFinish={async (form) => {
            await userSelfDelete({code: form.code}).then(res => {
                ToastSuccess(res.msg)
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
                await userSelfDeleteSendEmailCode().then(res => {
                    ToastSuccess(res.msg)
                })
            }}
        />
    </ModalForm>
}

const RequestSelfLoginRecordModalTitle = "登录记录"

function RequestSelfLoginRecordModal() {

    const [visible, setVisible] = useState(false);

    return (
        <RouteContext.Consumer>
            {(routeContextType: RouteContextType) => {
                return <>
                    <a onClick={() => {
                        setVisible(true)
                    }}>查看记录</a>
                    <Modal
                        width={1200}
                        title={RequestSelfLoginRecordModalTitle}
                        onCancel={() => setVisible(false)}
                        visible={visible}
                        maskClosable={false}
                        footer={false}
                        className={"noFooterModal"}
                    >
                        <ProTable<SysMenuDO, SysMenuPageDTO>
                            rowKey={"id"}
                            columnEmptyText={false}
                            revalidateOnFocus={false}
                            scroll={{y: 440}}
                            search={{
                                filterType: 'light',
                            }}
                            columns={[
                                {
                                    title: '序号',
                                    dataIndex: 'index',
                                    valueType: 'index',
                                    width: 50,
                                },
                                {
                                    title: '创建时间',
                                    dataIndex: 'createTime',
                                    sorter: true,
                                    valueType: 'fromNow',
                                    ellipsis: true,
                                    hideInSearch: true,
                                    width: 90,
                                },
                                {title: 'ip', dataIndex: 'ip', width: 120, ellipsis: true,},
                                {
                                    title: 'ip区域',
                                    dataIndex: 'region',
                                    ellipsis: true,
                                    copyable: true,
                                    width: 180,
                                    renderText: (text) => {
                                        return handlerRegion(text)
                                    }
                                },
                                {
                                    title: '来源',
                                    dataIndex: 'category',
                                    valueType: 'select',
                                    width: 120,
                                    ellipsis: true,
                                    fieldProps: {
                                        showSearch: true,
                                    },
                                    request: () => {
                                        return RequestGetDictList('request_category')
                                    }
                                },
                            ]}
                            pagination={{
                                showQuickJumper: true,
                                showSizeChanger: true,
                            }}
                            options={{
                                fullScreen: true,
                            }}
                            request={(params, sort, filter) => {
                                return sysRequestSelfLoginRecord({...params, sort})
                            }}
                        >
                        </ProTable>
                    </Modal>
                </>
            }}
        </RouteContext.Consumer>
    )
}
