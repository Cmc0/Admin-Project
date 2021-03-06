import React, {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ModalForm, ProFormDigit, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Form, Menu, Space} from "antd";
import {CalcOrderNo, GetIdListForHasChildrenNode} from "../../../../util/TreeUtil";
import {ColumnHeightOutlined, EllipsisOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import CommonConstant from "@/model/constant/CommonConstant";
import {AddOrderNo} from "@/model/dto/AddOrderNoDTO";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {
    sysDictAddOrderNo,
    sysDictDeleteByIdSet,
    sysDictInfoById,
    sysDictInsertOrUpdate,
    SysDictInsertOrUpdateDTO,
    SysDictPageDTO,
    sysDictTree,
    SysDictTreeVO
} from "@/api/SysDictController";
import TableColumnList from "@/page/sys/Dict/TableColumnList";
import SchemaFormColumnList, {InitForm} from "@/page/sys/Dict/SchemaFormColumnList";

export default function () {

    const [expandedRowKeys, setExpandedRowKeys] = useState<number[]>([]);

    const hasChildrenIdList = useRef<number[]>([]); // 有子节点的 idList

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysDictInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<SysDictInsertOrUpdateDTO>({})

    const sysDictTreeRef = useRef<SysDictTreeVO[]>([])

    function doGetSysDictTree() {
        sysDictTree({}).then(res => {
            sysDictTreeRef.current = res.data || []
        })
    }

    useEffect(() => {
        doGetSysDictTree()
    }, [])

    return <>
        <ProTable<SysDictTreeVO, SysDictPageDTO>
            actionRef={actionRef}
            rowKey={"id"}
            pagination={false}
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
                return sysDictTree({...params, sort})
            }}
            postData={(data) => {
                doGetSysDictTree()
                hasChildrenIdList.current = GetIdListForHasChildrenNode(data)
                setExpandedRowKeys(hasChildrenIdList.current) // 默认展开全部
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
                        currentForm.current = {type: 1}
                        CalcOrderNo(currentForm.current, {children: sysDictTreeRef.current});
                        setFormVisible(true)
                    }}>新建</Button>
                ],
            }}
            tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
                <Space size={16}>
                    <ModalForm<SysDictInsertOrUpdateDTO>
                        modalProps={{
                            maskClosable: false
                        }}
                        isKeyPressSubmit
                        width={CommonConstant.MODAL_FORM_WIDTH}
                        title={AddOrderNo}
                        trigger={<a>{AddOrderNo}</a>}
                        onFinish={async (form) => {
                            await sysDictAddOrderNo({
                                idSet: selectedRowKeys,
                                number: form.orderNo!
                            }).then(res => {
                                ToastSuccess(res.msg)
                                setTimeout(() => {
                                    actionRef.current?.reload()
                                }, CommonConstant.MODAL_ANIM_TIME)
                            })
                            return true
                        }}
                    >
                        <ProFormDigit label="排序号" name="orderNo" min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                      rules={[{required: true}]}/>
                    </ModalForm>
                    <a className={"red3"} onClick={() => {
                        execConfirm(() => {
                            return sysDictDeleteByIdSet({idSet: selectedRowKeys}).then(res => {
                                ToastSuccess(res.msg)
                                actionRef.current?.reload()
                                onCleanSelected()
                            })
                        }, undefined, `确定删除选中的【${selectedRowKeys.length}】项吗？`)
                    }}>批量删除</a>
                    <a onClick={onCleanSelected}>取消选择</a>
                </Space>
            )}
        >
        </ProTable>

        <BetaSchemaForm<SysDictInsertOrUpdateDTO>
            title={currentForm.current.id ? `编辑${currentForm.current.type === 1 ? '字典' : '字典项'}` : `新建${currentForm.current.type === 1 ? '字典' : '字典项'}`}
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
                                    return sysDictDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
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
                    await sysDictInfoById({id: currentForm.current.id}).then(res => {
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
                await sysDictInsertOrUpdate({...currentForm.current, ...form}).then(res => {
                    ToastSuccess(res.msg)
                    setTimeout(() => {
                        actionRef.current?.reload()
                    }, CommonConstant.MODAL_ANIM_TIME + 100)
                })
                return true
            }}
        />
    </>
}
