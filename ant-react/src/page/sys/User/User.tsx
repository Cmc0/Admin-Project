import React, {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ProTable} from "@ant-design/pro-components";
import {Button, Form, Space} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {
    sysUserDeleteByIdSet,
    sysUserInfoById,
    sysUserInsertOrUpdate,
    SysUserInsertOrUpdateDTO,
    sysUserPage,
    SysUserPageDTO,
    SysUserPageVO,
    sysUserRefreshJwtSecretSuf,
    sysUserResetAvatar
} from "@/api/SysUserController";
import TableColumnList, {SysUserUpdatePasswordModalForm} from "@/page/sys/User/TableColumnList";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import SchemaFormColumnList, {InitForm} from "@/page/sys/User/SchemaFormColumnList";
import {GetDeptDictList, GetJobDictList, GetRoleDictList, IMyOption, IMyTree} from "../../../../util/DictUtil";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";
import {useAppSelector} from "@/store";

export default function () {

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysUserInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)

    const currentForm = useRef<SysUserInsertOrUpdateDTO>({})

    const [expandedRowKeys, setExpandedRowKeys] = useState<number[]>([]);

    const deptDictListRef = useRef<IMyTree[]>([])
    const jobDictListRef = useRef<IMyTree[]>([])
    const roleDictListRef = useRef<IMyOption[]>([])

    function doGetDictList() {
        GetDeptDictList().then(res => {
            deptDictListRef.current = res
        })
        GetJobDictList().then(res => {
            jobDictListRef.current = res
        })
        GetRoleDictList().then(res => {
            roleDictListRef.current = res
        })
    }

    useEffect(() => {
        doGetDictList()
    }, [])

    return (
        <>
            <ProTable<SysUserPageVO, SysUserPageDTO>
                actionRef={actionRef}
                rowKey={"id"}
                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}
                columnEmptyText={false}
                rowSelection={{}}
                expandable={{
                    expandedRowKeys,
                    onExpandedRowsChange: (expandedRows) => {
                        setExpandedRowKeys(expandedRows as number[])
                    }
                }}
                revalidateOnFocus={false}
                columns={TableColumnList(currentForm, setFormVisible, actionRef)}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    return sysUserPage({...params, sort})
                }}
                toolbar={{
                    actions: [
                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {
                            currentForm.current = {}
                            setFormVisible(true)
                        }}>新建</Button>,
                    ],
                }}
                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
                    <Space size={16}>
                        <a onClick={() => {
                            execConfirm(() => {
                                return sysUserRefreshJwtSecretSuf({idSet: selectedRowKeys}).then(res => {
                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                })
                            }, undefined, `确定刷新选中的【${selectedRowKeys.length}】项的令牌吗？`)
                        }}>刷新令牌</a>
                        <SysUserUpdatePasswordModalForm idSet={selectedRowKeys as number[]} actionRef={actionRef}/>
                        <a onClick={() => {
                            execConfirm(() => {
                                return sysUserResetAvatar({idSet: selectedRowKeys}).then(res => {
                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                })
                            }, undefined, `确定重置选中的【${selectedRowKeys.length}】项的头像吗？`)
                        }}>重置头像</a>
                        <a className={"red3"} onClick={() => {
                            execConfirm(() => {
                                return sysUserDeleteByIdSet({idSet: selectedRowKeys}).then(res => {
                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()
                                })
                            }, undefined, `确定注销选中的【${selectedRowKeys.length}】项吗？`)
                        }}>批量注销</a>
                        <a onClick={onCleanSelected}>取消选择</a>
                    </Space>
                )}
            >
            </ProTable>

            <BetaSchemaForm<SysUserInsertOrUpdateDTO>
                title={currentForm.current.id ? "编辑用户" : "新建用户"}
                layoutType={"ModalForm"}
                grid
                rowProps={{
                    gutter: 16,
                }}
                colProps={{
                    span: 12
                }}
                modalProps={{
                    maskClosable: false,
                }}
                form={useForm}
                isKeyPressSubmit
                submitter={{
                    render: (props, dom) => {
                        return [
                            ...dom,
                            <Button
                                key="1"
                                onClick={() => {
                                    props.reset();
                                }}
                            >
                                重置
                            </Button>,
                            currentForm.current.id ? <Button
                                key="2"
                                type="primary"
                                danger
                                onClick={() => {
                                    execConfirm(async () => {
                                        return sysUserDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
                                            setFormVisible(false)
                                            ToastSuccess(res.msg)
                                            setTimeout(() => {
                                                actionRef.current?.reload()
                                            }, CommonConstant.MODAL_ANIM_TIME + 100)
                                        })
                                    }, undefined, `确定注销【${currentForm.current.nickname}】吗？`)
                                }}>
                                注销
                            </Button> : null
                        ]
                    },
                }}
                params={new Date()} // 目的：为了打开页面时，执行 request方法
                request={async () => {

                    useForm.resetFields()

                    if (currentForm.current.id) {
                        await sysUserInfoById({id: currentForm.current.id}).then(res => {
                            currentForm.current = res
                        })
                    }
                    useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm
                }}
                visible={formVisible}
                onVisibleChange={setFormVisible}
                columns={SchemaFormColumnList(useForm, deptDictListRef, jobDictListRef, roleDictListRef)}
                onFinish={async (form) => {

                    const formTemp = {...form}

                    if (!currentForm.current.id) {
                        const date = new Date()
                        formTemp.origPassword = RSAEncryptPro(formTemp.password, rsaPublicKey, date)
                        formTemp.password = PasswordRSAEncrypt(formTemp.password, rsaPublicKey, date)
                    }

                    await sysUserInsertOrUpdate({...currentForm.current, ...formTemp}).then(res => {
                        ToastSuccess(res.msg)
                        setTimeout(() => {
                            actionRef.current?.reload()
                        }, CommonConstant.MODAL_ANIM_TIME + 100)
                    })
                    return true
                }}
            />
        </>
    )
}
