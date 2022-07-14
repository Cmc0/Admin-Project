import {ProColumns} from "@ant-design/pro-components";
import {GetUserDictList, RequestGetDictList} from "../../../../util/DictUtil";
import React from "react";
import {SysBulletinDO, SysBulletinUserSelfPageDTO} from "@/api/SysBulletinController";

const TableColumnList = (): ProColumns<SysBulletinDO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {
        title: '公告类型', dataIndex: 'type',
        valueType: 'select',
        fieldProps: {
            showSearch: true,
        },
        request: () => {
            return RequestGetDictList('bulletin_type')
        }
    },
    {title: '标题', dataIndex: 'title'},
    {title: '公告内容', dataIndex: 'content'},
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
                } as SysBulletinUserSelfPageDTO
            }
        }
    },
    {
        title: '创建人', dataIndex: 'createId', valueType: 'select',
        fieldProps: {
            showSearch: true,
        },
        request: () => {
            return GetUserDictList()
        }
    },
];

export default TableColumnList
