import {ActionType, ProColumns} from "@ant-design/pro-components";
import {Dropdown, Menu, Tag} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {CalcOrderNo} from "../../../../util/TreeUtil";
import {sysDictDeleteByIdSet, SysDictInsertOrUpdateDTO, SysDictTreeVO} from "@/api/SysDictController";

const TableColumnList = (currentForm: React.MutableRefObject<SysDictInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysDictTreeVO>[] => [
    {
        title: '名称',
        dataIndex: 'name',
    },
    {
        title: '字典Key/字典项Value', dataIndex: 'dictKeyOrValue',
        renderText: (text, record) => {
            return record.type === 1 ? record.dictKey : record.value
        },
        hideInSearch: true
    },
    {
        title: '字典Key', dataIndex: 'dictKey', hideInTable: true
    },
    {
        title: '类别', dataIndex: "type",
        render: (dom, entity) =>
            <Tag color={entity.type === 1 ? 'purple' : 'green'}>{entity.type === 1 ? '字典' : '字典项'}</Tag>
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },
    {
        title: '操作',
        width: 180,
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={() => {
                currentForm.current = {id: entity.id}
                setFormVisible(true)
            }}>编辑</a>,
            <a key="2" className={"red3"} onClick={() => {
                execConfirm(() => {
                    return sysDictDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除【${entity.name}】吗？`)
            }}>删除</a>,
            (
                entity.type === 1 && <Dropdown key="3" overlay={<Menu items={[
                    {
                        key: '1',
                        label: <a onClick={() => {
                            currentForm.current = {dictKey: entity.dictKey, type: 2, value: 1}
                            CalcOrderNo(currentForm.current, entity, ({item}) => {
                                if (item.value! >= currentForm.current!.value!) {
                                    currentForm.current!.value = Number(item.value) + 1 // 如果存在字典项，那么则取最大的 value + 1
                                }
                            })
                            setFormVisible(true)
                        }}>
                            添加字典项
                        </a>,
                    },
                ]}>
                </Menu>}>
                    <a><EllipsisOutlined/></a>
                </Dropdown>
            )
        ],
    },
];

export default TableColumnList
