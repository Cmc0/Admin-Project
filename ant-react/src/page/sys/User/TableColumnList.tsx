import {ActionType, ModalForm, ProColumns, ProFormText} from "@ant-design/pro-components";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {SysRequestPageDTO} from "@/api/SysRequestController";
import {Dropdown, Menu} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {
    sysUserDeleteByIdSet,
    SysUserInsertOrUpdateDTO,
    SysUserPageVO,
    sysUserResetAvatar,
    sysUserUpdatePassword,
    SysUserUpdatePasswordDTO
} from "@/api/SysUserController";
import CommonConstant from "@/model/constant/CommonConstant";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";

const UpdatePasswordTitle = "修改密码"

const TableColumnList = (currentForm: React.MutableRefObject<SysUserInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>, rsaPublicKey: string): ProColumns<SysUserPageVO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {
        title: '头像', dataIndex: 'avatarUrl', valueType: 'avatar', hideInSearch: true,
        renderText: (text) => {
            return text ? text : CommonConstant.RANDOM_AVATAR_URL
        }
    },
    {title: '昵称', dataIndex: 'nickname'},
    {
        title: '邮箱',
        dataIndex: 'email',
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
        renderText: (text, record) => {
            return text ? text : record.createTime
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
                } as SysRequestPageDTO
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
                    label: <ModalForm<SysUserUpdatePasswordDTO>
                        modalProps={{
                            maskClosable: false
                        }}
                        isKeyPressSubmit
                        width={450}
                        title={UpdatePasswordTitle}
                        trigger={<a>{UpdatePasswordTitle}</a>}
                        onFinish={async (form) => {
                            const formTemp = {...form}
                            if (formTemp.newPassword) {
                                const date = new Date()
                                formTemp.newOrigPassword = RSAEncryptPro(formTemp.newPassword, rsaPublicKey, date)
                                formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword, rsaPublicKey, date)
                            }
                            await sysUserUpdatePassword({
                                ...formTemp,
                                idSet: [entity.id!]
                            }).then(res => {
                                ToastSuccess(res.msg)
                            })
                            return true
                        }}
                    >
                        <ProFormText label="新密码" name="newPassword"/>
                    </ModalForm>
                },
            ]}>
            </Menu>}>
                <a><EllipsisOutlined/></a>
            </Dropdown>,
        ],
    },
];

export default TableColumnList
