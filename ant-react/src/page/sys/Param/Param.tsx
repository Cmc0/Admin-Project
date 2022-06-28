import React, {useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ProTable} from "@ant-design/pro-components";
import {Button, Form} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {
    sysParamDeleteByIdSet,
    SysParamDO,
    sysParamInfoById,
    sysParamInsertOrUpdate,
    SysParamInsertOrUpdateDTO,
    sysParamPage,
    SysParamPageDTO
} from "@/api/SysParamController";
import SchemaFormColumnList, {InitForm} from "@/page/sys/Param/SchemaFormColumnList";
import TableColumnList from "@/page/sys/Param/TableColumnList";

export default function () {

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysParamInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<SysParamInsertOrUpdateDTO>({})

    return (
        <>
            <ProTable<SysParamDO, SysParamPageDTO>
                actionRef={actionRef}
                rowKey={"id"}
                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}
                columnEmptyText={false}
                revalidateOnFocus={false}
                columns={TableColumnList(currentForm, setFormVisible, actionRef)}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    return sysParamPage({...params, sort})
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

            <BetaSchemaForm<SysParamInsertOrUpdateDTO>
                title={currentForm.current.id ? "编辑系统参数" : "新建系统参数"}
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
                                        return sysParamDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
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
                        await sysParamInfoById({id: currentForm.current.id}).then(res => {
                            currentForm.current = res
                        })
                    }
                    useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm
                }}
                visible={formVisible}
                onVisibleChange={setFormVisible}
                columns={SchemaFormColumnList()}
                onFinish={async (form) => {
                    await sysParamInsertOrUpdate({...currentForm.current, ...form}).then(res => {
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
