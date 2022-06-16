import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {ActionType, BetaSchemaForm, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Form, Menu, Space} from "antd";
import {
    menuDeleteByIdSet,
    menuInfoById,
    menuInsertOrUpdate,
    MenuInsertOrUpdateDTO,
    MenuPageDTO,
    menuTree
} from "@/api/MenuController";
import {ColumnHeightOutlined, EllipsisOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import React, {useRef, useState} from "react";
import {GetIdListForHasChildrenNode} from "../../../../util/TreeUtil";
import TableColumnList from "@/page/sys/Menu/TableColumnList";
import SchemaFormColumnList, {InitForm} from "@/page/sys/Menu/SchemaFormColumnList";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";


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

    const [treeList, setTreeList] = useState<BaseMenuDO[]>([]);

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const [useForm] = Form.useForm<MenuInsertOrUpdateDTO>();

    const actionRef = useRef<ActionType>(null)

    const currentForm = useRef<MenuInsertOrUpdateDTO | null>({})

    return <>
        <ProTable<BaseMenuDO, MenuPageDTO>
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
                return menuTree({...params, sort})
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
                    <Button icon={<PlusOutlined/>} type="primary" onClick={() => {
                        if (!currentForm.current) {
                            currentForm.current = {}
                        }
                        currentForm.current.id = undefined
                        setFormVisible(true)
                    }}>新建</Button>
                ],
            }}
            tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
                <Space size={16}>
                    <a onClick={() => {
                        execConfirm(() => {
                            return menuDeleteByIdSet({idSet: selectedRowKeys}).then(res => {
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

        <BetaSchemaForm<MenuInsertOrUpdateDTO>
            title={"新建菜单"}
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
                forceRender: true, // 不加 useForm会报警告
            }}
            form={useForm}
            autoFocusFirstInput={false}
            shouldUpdate={false}
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
                        currentForm.current?.id ? <Button
                            key="extra-del"
                            type="primary"
                            danger
                            onClick={() => {
                                execConfirm(async () => {
                                    return menuDeleteByIdSet({idSet: [currentForm.current!.id!]}).then(res => {
                                        setFormVisible(false)
                                        ToastSuccess(res.msg)
                                        setTimeout(() => {
                                            actionRef.current?.reload().then(() => {
                                                currentForm.current = {}
                                            })
                                        }, CommonConstant.MODAL_ANIM_TIME) // 要等 modal关闭动画完成
                                    })
                                }, undefined, `确定删除【${currentForm.current!.name}】吗？`)
                            }}>
                            删除
                        </Button> : null
                    ]
                },
            }}
            params={{id: currentForm.current?.id, parentId: currentForm.current?.parentId}}
            request={async () => {

                useForm.resetFields()

                if (currentForm.current!.id) {
                    await menuInfoById({id: currentForm.current!.id}).then(res => {
                        currentForm.current = res
                        useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current
                    })
                } else {
                    if (currentForm.current!.parentId) {
                        currentForm.current = {parentId: currentForm.current!.parentId}
                        useForm.setFieldsValue(currentForm.current)
                    } else {
                        currentForm.current = {}
                    }
                }

                return InitForm
            }}
            visible={formVisible}
            onVisibleChange={setFormVisible}
            columns={SchemaFormColumnList(treeList, useForm)}
            onFinish={async (form) => {
                await menuInsertOrUpdate({...currentForm.current, ...form}).then(res => {
                    setFormVisible(false)
                    ToastSuccess(res.msg)
                    setTimeout(() => {
                        actionRef.current?.reload()
                    }, CommonConstant.MODAL_ANIM_TIME) // 要等 modal关闭动画完成
                })
            }}
        />
    </>

}
