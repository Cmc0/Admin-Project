import {ActionType, ProColumns} from "@ant-design/pro-components";
import {BulletinTypeDict, GetUserDictList, RequestGetDictList} from "../../../../util/DictUtil";
import React from "react";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {
    sysBulletinDeleteByIdSet,
    SysBulletinDO,
    SysBulletinInsertOrUpdateDTO,
    SysBulletinPageDTO
} from "@/api/SysBulletinController";

const TableColumnList = (currentForm: React.MutableRefObject<SysBulletinInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysBulletinDO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {title: '主键id', dataIndex: 'id'},
    {
        title: '公告类型', dataIndex: 'type',
        valueType: 'select',
        request: () => {
            return RequestGetDictList('bulletin_type')
        }
    },
    {title: '标题', dataIndex: 'title'},
    {title: '公告内容', dataIndex: 'content'},
    {
        title: '状态',
        dataIndex: 'status',
        valueEnum: BulletinTypeDict
    },
    {
        title: '发布时间',
        dataIndex: 'publishTime',
        valueType: 'fromNow',
        sorter: true,
        hideInSearch: true,
        defaultSortOrder: 'descend',
    },
    {
        title: '发布时间', dataIndex: 'publishTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    ptBeginTime: value[0],
                    ptEndTime: value[1],
                } as SysBulletinPageDTO
            }
        }
    },
    {
        title: 'xxlJobId', dataIndex: 'xxlJobId', renderText: (text) => {
            return text === -1 ? "" : text
        }
    },
    {title: '备注', dataIndex: 'remark'},
    {
        title: '创建人', dataIndex: 'createId', valueType: 'select',
        request: () => {
            return GetUserDictList()
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
                    return sysBulletinDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除，主键id为【${entity.id}】的数据吗？`)
            }}>删除</a>,
        ],
    },
];

export default TableColumnList
