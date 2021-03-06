import {ActionType, ProColumns} from "@ant-design/pro-components";
import React from "react";
import {handlerRegion} from "../../../../util/StrUtil";
import {GetUserDictList, RequestGetDictList, YesNoDict} from "../../../../util/DictUtil";
import {SysRequestDO, SysRequestPageDTO} from "@/api/SysRequestController";

const TableColumnList = (actionRef: React.RefObject<ActionType>): ProColumns<SysRequestDO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {title: '接口名', dataIndex: 'name'},
    {title: 'uri', dataIndex: 'uri', copyable: true},
    {
        title: '用户昵称', dataIndex: 'createId', valueType: 'select',
        fieldProps: {
            showSearch: true,
        },
        request: () => {
            return GetUserDictList()
        }
    },
    {title: 'ip', dataIndex: 'ip'},
    {
        title: 'ip区域',
        dataIndex: 'region',
        renderText: (text) => {
            return handlerRegion(text)
        }
    },
    {
        title: '来源',
        dataIndex: 'category',
        valueType: 'select',
        fieldProps: {
            showSearch: true,
        },
        request: () => {
            return RequestGetDictList('request_category')
        }
    },
    {
        title: '请求成功',
        dataIndex: 'successFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '耗时/失败信息', dataIndex: 'timeNumber', sorter: true, renderText: (text, record) => {
            if (record.successFlag) {
                return record.timeStr
            }
            return record.errorMsg
        },
        hideInSearch: true,
    },
    {
        title: '耗时(ms)', dataIndex: 'timeNumberRange', hideInTable: true, valueType: 'digitRange', search: {
            transform: (value) => {
                return {
                    beginTimeNumber: value[0],
                    endTimeNumber: value[1],
                } as SysRequestPageDTO
            }
        }
    },
    {title: '创建时间', dataIndex: 'createTime', sorter: true, valueType: 'fromNow', hideInSearch: true},
    {
        title: '创建时间', dataIndex: 'createTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    beginCreateTime: value[0],
                    endCreateTime: value[1],
                } as SysRequestPageDTO
            }
        }
    },
];

export default TableColumnList
