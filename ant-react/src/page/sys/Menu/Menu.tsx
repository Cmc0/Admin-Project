import SysMenuDO from "@/model/entity/SysMenuDO";
import {ActionType, BetaSchemaForm, ColumnsState, ModalForm, ProFormDigit, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Form, Menu, Space} from "antd";
import {ColumnHeightOutlined, EllipsisOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import React, {useRef, useState} from "react";
import {CalcOrderNo, GetIdListForHasChildrenNode} from "../../../../util/TreeUtil";
import TableColumnList from "@/page/sys/Menu/TableColumnList";
import SchemaFormColumnList, {InitForm} from "@/page/sys/Menu/SchemaFormColumnList";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {
    sysMenuAddOrderNo,
    sysMenuDeleteByIdSet,
    sysMenuInfoById,
    sysMenuInsertOrUpdate,
    SysMenuInsertOrUpdateDTO,
    SysMenuPageDTO,
    sysMenuTree
} from "@/api/SysMenuController";

const AddOrderNo = "累加排序号"

export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>(
        {
            firstFlag: {show: false,},
            authFlag: {show: false,},
            linkFlag: {show: false,},
            updateTime: {show: false,},
        });

    const [expandedRowKeys, setExpandedRowKeys] = useState<number[]>([]);

    const hasChildrenIdList = useRef<number[]>([]); // 有子节点的 idList

    const [treeList, setTreeList] = useState<SysMenuDO[]>([]);

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const [useForm] = Form.useForm<SysMenuInsertOrUpdateDTO>();

    const actionRef = useRef<ActionType>(null)

    const currentForm = useRef<SysMenuInsertOrUpdateDTO>({})

    return <>
        <ProTable<SysMenuDO, SysMenuPageDTO>
            actionRef={actionRef}
            rowKey={"id"}
            pagination={{
                showQuickJumper: true,
            }}
            columnEmptyText={false}
            columnsState={{
                value: columnsStateMap,
                onChange: setColumnsStateMap,
            }}
            expandable={{
                expandedRowKeys,
                onExpandedRowsChange: (expandedRows) => {
                    setExpandedRowKeys(expandedRows as number[])
                }
            }}
            revalidateOnFocus={false}
            rowSelection={{}}
            columns={TableColumnList(currentForm, setFormVisible, actionRef)}
            options={{
                fullScreen: true,
            }}
            request={(params, sort, filter) => {
                return sysMenuTree({...params, sort})
            }}
            postData={(data) => {
                setTreeList(data)
                hasChildrenIdList.current = GetIdListForHasChildrenNode(data)
                return data
            }}
            toolbar={{
                title:
                    <Dropdown
                        overlay={<Menu items={[
                            {
                                key: 'expandAll',
                                label: <a onClick={() => {
                                    setExpandedRowKeys(hasChildrenIdList.current)
                                }}>
                                    展开全部
                                </a>,
                                icon: <ColumnHeightOutlined/>
                            },
                            {
                                key: 'collapseAll',
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
                        CalcOrderNo(currentForm.current, {children: treeList});
                        setFormVisible(true)
                    }}>新建</Button>
                ],
            }}
            tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
                <Space size={16}>
                    <ModalForm<SysMenuInsertOrUpdateDTO>
                        modalProps={{
                            maskClosable: false
                        }}
                        isKeyPressSubmit
                        width={450}
                        title={AddOrderNo}
                        trigger={<a>{AddOrderNo}</a>}
                        onFinish={async (form) => {
                            await sysMenuAddOrderNo({
                                idSet: selectedRowKeys,
                                number: form.orderNo!
                            }).then(res => {
                                ToastSuccess(res.msg)
                                setTimeout(() => {
                                    actionRef.current?.reload()
                                }, CommonConstant.MODAL_ANIM_TIME) // 要等 modal关闭动画完成
                            })
                            return true
                        }}
                    >
                        <ProFormDigit label="排序号" name="orderNo" min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                      rules={[{required: true}]}/>
                    </ModalForm>
                    <a onClick={() => {
                        execConfirm(() => {
                            return sysMenuDeleteByIdSet({idSet: selectedRowKeys}).then(res => {
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

        <BetaSchemaForm<SysMenuInsertOrUpdateDTO>
            title={currentForm.current.id ? "新建菜单" : "编辑菜单"}
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
            onValuesChange={(changedValues, allValues) => {
                if (allValues.path && allValues.path.startsWith("http")) {
                    useForm.setFieldsValue({linkFlag: true})
                }
            }}
            submitter={{
                render: (props, dom) => {
                    return [
                        ...dom,
                        <Button
                            key="extra-reset"
                            onClick={() => {
                                props.reset();
                            }}
                        >
                            重置
                        </Button>,
                        currentForm.current.id ? <Button
                            key="extra-del"
                            type="primary"
                            danger
                            onClick={() => {
                                execConfirm(async () => {
                                    return sysMenuDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
                                        setFormVisible(false)
                                        ToastSuccess(res.msg)
                                        setTimeout(() => {
                                            actionRef.current?.reload()
                                        }, CommonConstant.MODAL_ANIM_TIME) // 要等 modal关闭动画完成
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
                    await sysMenuInfoById({id: currentForm.current.id}).then(res => {
                        currentForm.current = res
                    })
                }
                useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                return InitForm
            }}
            visible={formVisible}
            onVisibleChange={setFormVisible}
            columns={SchemaFormColumnList(treeList, useForm)}
            onFinish={async (form) => {
                await sysMenuInsertOrUpdate({...currentForm.current, ...form}).then(res => {
                    ToastSuccess(res.msg)
                    setTimeout(() => {
                        actionRef.current?.reload()
                    }, CommonConstant.MODAL_ANIM_TIME + 100) // 要等 modal关闭动画完成
                })
                return true
            }}
        />
    </>

}
