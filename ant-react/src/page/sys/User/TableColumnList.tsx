import {ActionType, ModalForm, ProColumns, ProFormText} from "@ant-design/pro-components";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {Dropdown, Menu} from "antd";
import {EllipsisOutlined, EyeOutlined} from "@ant-design/icons/lib";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {
    sysUserDeleteByIdSet,
    SysUserInsertOrUpdateDTO,
    SysUserPageDTO,
    SysUserPageVO,
    sysUserRefreshJwtSecretSuf,
    sysUserResetAvatar,
    sysUserUpdatePassword,
    SysUserUpdatePasswordDTO
} from "@/api/SysUserController";
import CommonConstant from "@/model/constant/CommonConstant";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";
import {useAppSelector} from "@/store";
import {ValidatorUtil} from "../../../../util/ValidatorUtil";
import {GetPublicDownFileUrl} from "../../../../util/FileUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysUserInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysUserPageVO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {
        title: '头像', dataIndex: 'avatarUrl', valueType: 'image', hideInSearch: true,
        fieldProps: {
            preview: {
                mask: <EyeOutlined title={"预览"}/>
            }
        },
        renderText: (text) => {
            return text ? GetPublicDownFileUrl(text) : CommonConstant.RANDOM_AVATAR_URL
        }
    },
    {title: '昵称', dataIndex: 'nickname'},
    {
        title: '邮箱',
        dataIndex: 'email',
    },
    {
        title: '设置密码',
        dataIndex: 'passwordFlag',
        valueEnum: YesNoDict
    },
    {
        title: '账号正常',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '最近活跃',
        dataIndex: 'lastActiveTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
        defaultSortOrder: 'descend',
    },
    {
        title: '最近活跃', dataIndex: 'lastActiveTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    beginLastActiveTime: value[0],
                    endLastActiveTime: value[1],
                } as SysUserPageDTO
            }
        }
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
    },
    {
        title: '创建时间', dataIndex: 'createTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    beginCreateTime: value[0],
                    endCreateTime: value[1],
                } as SysUserPageDTO
            }
        }
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
    },
    {
        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={() => {
                currentForm.current = {id: entity.id}
                setFormVisible(true)
            }}>编辑</a>,
            <a key="2" className={"red3"} onClick={() => {
                execConfirm(() => {
                    return sysUserDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定注销【${entity.nickname}】吗？`)
            }}>注销</a>,
            ,
            <Dropdown key="3" overlay={<Menu items={[
                {
                    key: '1',
                    label: <a onClick={() => {
                        execConfirm(() => {
                            return sysUserResetAvatar({idSet: [entity.id!]}).then(res => {
                                ToastSuccess(res.msg)
                                actionRef.current?.reload()
                            })
                        }, undefined, `确定重置【${entity.nickname}】的头像吗？`)
                    }}>重置头像</a>,
                },
                {
                    key: '2',
                    label: <SysUserUpdatePasswordModalForm idSet={[entity.id!]} actionRef={actionRef}/>
                },
                {
                    key: '3',
                    label: <a onClick={() => {
                        execConfirm(() => {
                            return sysUserRefreshJwtSecretSuf({idSet: [entity.id!]}).then(res => {
                                ToastSuccess(res.msg)
                                actionRef.current?.reload()
                            })
                        }, undefined, `确定刷新【${entity.nickname}】的令牌吗？`)
                    }}>刷新令牌</a>
                },
            ]}>
            </Menu>}>
                <a><EllipsisOutlined/></a>
            </Dropdown>,
        ],
    },
];

export default TableColumnList

const SysUserUpdatePasswordTitle = "修改密码"

interface ISysUserUpdatePasswordModalForm {
    idSet: number[]
    actionRef: React.RefObject<ActionType>
}

export function SysUserUpdatePasswordModalForm(props: ISysUserUpdatePasswordModalForm) {

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)

    return <ModalForm<SysUserUpdatePasswordDTO>
        modalProps={{
            maskClosable: false
        }}
        isKeyPressSubmit
        width={CommonConstant.MODAL_FORM_WIDTH}
        title={SysUserUpdatePasswordTitle}
        trigger={<a>{SysUserUpdatePasswordTitle}</a>}
        onFinish={async (form) => {
            const formTemp = {...form}
            if (formTemp.newPassword) {
                const date = new Date()
                formTemp.newOrigPassword = RSAEncryptPro(formTemp.newPassword, rsaPublicKey, date)
                formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword, rsaPublicKey, date)
            }
            await sysUserUpdatePassword({
                ...formTemp,
                idSet: props.idSet
            }).then(res => {
                ToastSuccess(res.msg)
                setTimeout(() => {
                    props.actionRef.current?.reload()
                }, CommonConstant.MODAL_ANIM_TIME)
            })
            return true
        }}
    >
        <ProFormText label="新密码" tooltip={"可以为空"} name="newPassword"
                     rules={[{validator: ValidatorUtil.passwordCanNullValidate}]}/>
    </ModalForm>
}
