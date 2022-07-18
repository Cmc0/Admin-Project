import React, {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ProTable} from "@ant-design/pro-components";
import {Button, Form} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {
    sysBulletinDeleteByIdSet,
    SysBulletinDO,
    sysBulletinInfoById,
    sysBulletinInsertOrUpdate,
    SysBulletinInsertOrUpdateDTO,
    sysBulletinPage,
    SysBulletinPageDTO
} from "@/api/SysBulletinController";
import TableColumnList from "@/page/sys/Bulletin/TableColumnList";
import SchemaFormColumnList, {InitForm} from "@/page/sys/Bulletin/SchemaFormColumnList";
import {RequestGetDictList} from "../../../../util/DictUtil";
import DictListVO from "@/model/vo/DictListVO";

export default function () {

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysBulletinInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<SysBulletinInsertOrUpdateDTO>({})

    const bulletinTypeDictListRef = useRef<DictListVO[]>([])

    function doGetDictList() {
        RequestGetDictList('bulletin_type').then(res => {
            bulletinTypeDictListRef.current = res
        })
    }

    useEffect(() => {
        doGetDictList()
    }, [])

    return (
        <>
            <ProTable<SysBulletinDO, SysBulletinPageDTO>
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
                    doGetDictList()
                    return sysBulletinPage({...params, sort})
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

            <BetaSchemaForm<SysBulletinInsertOrUpdateDTO>
                title={currentForm.current.id ? "编辑公告" : "新建公告"}
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
                                        return sysBulletinDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
                                            setFormVisible(false)
                                            ToastSuccess(res.msg)
                                            setTimeout(() => {
                                                actionRef.current?.reload()
                                            }, CommonConstant.MODAL_ANIM_TIME + 100)
                                        })
                                    }, undefined, `确定删除【${currentForm.current.title}】吗？`)
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
                        await sysBulletinInfoById({id: currentForm.current.id}).then(res => {
                            currentForm.current = res
                        })
                    }
                    useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm
                }}
                visible={formVisible}
                onVisibleChange={setFormVisible}
                columns={SchemaFormColumnList(bulletinTypeDictListRef)}
                onFinish={async (form) => {
                    await sysBulletinInsertOrUpdate({...currentForm.current, ...form}).then(res => {
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
