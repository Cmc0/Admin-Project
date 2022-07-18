import React, {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Form, Menu, Space, Typography} from "antd";
import {ColumnHeightOutlined, EllipsisOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
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
import {
    getByValueFromDictListPro,
    GetDeptDictList,
    GetJobDictList,
    GetRoleDictList,
    IMyTree
} from "../../../../util/DictUtil";
import {PasswordRSAEncrypt, RSAEncryptPro} from "../../../../util/RsaUtil";
import {useAppSelector} from "@/store";
import DictListVO from "@/model/vo/DictListVO";
import {ListToTree} from "../../../../util/TreeUtil";

export default function () {

    const [expandedRowKeys, setExpandedRowKeys] = useState<number[]>([]);

    const hasChildrenIdList = useRef<number[]>([]); // 有子节点的 idList

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysUserInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const rsaPublicKey = useAppSelector((state) => state.common.rsaPublicKey)

    const currentForm = useRef<SysUserInsertOrUpdateDTO>({})

    const deptDictTreeListRef = useRef<IMyTree[]>([])
    const jobDictTreeListRef = useRef<IMyTree[]>([])
    const roleDictListRef = useRef<DictListVO<number>[]>([])

    const deptDictListRef = useRef<DictListVO<number>[]>([])
    const jobDictListRef = useRef<DictListVO<number>[]>([])

    function doGetDictList() {
        GetDeptDictList(false).then(res => {
            deptDictListRef.current = res
            deptDictTreeListRef.current = ListToTree(res)
        })
        GetJobDictList(false).then(res => {
            jobDictListRef.current = res
            jobDictTreeListRef.current = ListToTree(res)
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
                    },
                    expandedRowRender: record => (
                        <div className={"flex-c"}>
                            <span>
                                <Typography.Text mark>
                                    部门
                                </Typography.Text>
                                <Typography.Text type="secondary">
                                    ：{getByValueFromDictListPro(deptDictListRef.current, record.deptIdSet)}
                                </Typography.Text>
                            </span>
                            <span>
                                <Typography.Text mark>
                                    岗位
                                </Typography.Text>
                                <Typography.Text type="secondary">
                                    ：{getByValueFromDictListPro(jobDictListRef.current, record.jobIdSet)}
                                </Typography.Text>
                            </span>
                            <span>
                                <Typography.Text mark>
                                    角色
                                </Typography.Text>
                                <Typography.Text type="secondary">
                                    ：{getByValueFromDictListPro(roleDictListRef.current, record.roleIdSet)}
                                </Typography.Text>
                            </span>
                        </div>
                    )
                }}
                revalidateOnFocus={false}
                columns={TableColumnList(currentForm, setFormVisible, actionRef)}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    return sysUserPage({...params, sort})
                }}
                postData={(data) => {
                    hasChildrenIdList.current = data.map(it => it.id)
                    return data
                }}
                toolbar={{
                    title:
                        <Dropdown
                            overlay={<Menu items={[
                                {
                                    key: '1',
                                    label: <a onClick={() => {
                                        setExpandedRowKeys(hasChildrenIdList.current)
                                    }}>
                                        展开全部
                                    </a>,
                                    icon: <ColumnHeightOutlined/>
                                },
                                {
                                    key: '2',
                                    label: <a onClick={() => {
                                        setExpandedRowKeys([])
                                    }}>
                                        收起全部
                                    </a>,
                                    icon: <VerticalAlignMiddleOutlined/>
                                },
                            ]}/>}
                        >
                            <Button size={"small"} icon={<EllipsisOutlined/>}/>
                        </Dropdown>,
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
                columns={SchemaFormColumnList(useForm, deptDictTreeListRef, jobDictTreeListRef, roleDictListRef)}
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
