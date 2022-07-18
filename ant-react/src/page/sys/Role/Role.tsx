import React, {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Button, Form} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {
    sysRoleDeleteByIdSet,
    SysRoleDO,
    sysRoleInfoById,
    sysRoleInsertOrUpdate,
    SysRoleInsertOrUpdateDTO,
    sysRolePage,
    SysRolePageDTO
} from "@/api/SysRoleController";
import TableColumnList from "@/page/sys/Role/TableColumnList";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import SchemaFormColumnList, {InitForm} from "@/page/sys/Role/SchemaFormColumnList";
import {GetMenuDictTreeList, GetUserDictList, IMyTree} from "../../../../util/DictUtil";
import DictListVO from "@/model/vo/DictListVO";

export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>(
        {
            defaultFlag: {show: false,},
        });

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysRoleInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<SysRoleInsertOrUpdateDTO>({})

    const menuDictListRef = useRef<IMyTree[]>([])
    const userDictListRef = useRef<DictListVO[]>([])

    function doGetDictList() {
        GetMenuDictTreeList().then(res => {
            menuDictListRef.current = res
        })
        GetUserDictList(false).then(res => {
            userDictListRef.current = res
        })
    }

    useEffect(() => {
        doGetDictList()
    }, [])

    return (
        <>
            <ProTable<SysRoleDO, SysRolePageDTO>
                actionRef={actionRef}
                rowKey={"id"}
                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}
                columnEmptyText={false}
                columnsState={{
                    value: columnsStateMap,
                    onChange: setColumnsStateMap,
                }}
                revalidateOnFocus={false}
                columns={TableColumnList(currentForm, setFormVisible, actionRef)}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    doGetDictList()
                    return sysRolePage({...params, sort})
                }}
                toolbar={{
                    actions: [
                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {
                            currentForm.current = {}
                            setFormVisible(true)
                        }}>新建</Button>
                    ],
                }}
            >
            </ProTable>

            <BetaSchemaForm<SysRoleInsertOrUpdateDTO>
                title={currentForm.current.id ? "编辑角色" : "新建角色"}
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
                                        return sysRoleDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
                                            setFormVisible(false)
                                            ToastSuccess(res.msg)
                                            setTimeout(() => {
                                                actionRef.current?.reload()
                                            }, CommonConstant.MODAL_ANIM_TIME + 100)
                                        })
                                    }, undefined, `确定删除【${currentForm.current.name}】吗？`)
                                }}>
                                删除
                            </Button> : null
                        ]
                    },
                }}
                params={new Date()} // 目的：为了打开页面时，执行 request方法
                request={async () => {

                    useForm.resetFields()

                    if (currentForm.current.id) {
                        await sysRoleInfoById({id: currentForm.current.id}).then(res => {
                            currentForm.current = res
                        })
                    }
                    useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm
                }}
                visible={formVisible}
                onVisibleChange={setFormVisible}
                columns={SchemaFormColumnList(menuDictListRef, userDictListRef)}
                onFinish={async (form) => {
                    await sysRoleInsertOrUpdate({...currentForm.current, ...form}).then(res => {
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
