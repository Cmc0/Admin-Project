import {ActionType, ProColumns} from "@ant-design/pro-components";
import React from "react";
import {SysWebSocketDO, sysWebSocketRetreatByIdSet} from "@/api/SysWebSocketController";
import {handlerRegion} from "../../../../util/StrUtil";
import {GetUserDictList, RequestGetDictList, WebSocketTypeDict, YesNoBaseDict} from "../../../../util/DictUtil";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {SysRequestPageDTO} from "@/api/SysRequestController";

const TableColumnList = (actionRef: React.RefObject<ActionType>): ProColumns<SysWebSocketDO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {title: 'id', dataIndex: 'id'},
    {
        title: '用户昵称', dataIndex: 'createId', valueType: 'select',
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
    {title: '浏览器', dataIndex: 'browser'},
    {title: '操作系统', dataIndex: 'os'},
    {
        title: '移动端网页',
        dataIndex: 'mobileFlag',
        valueEnum: YesNoBaseDict,
    },
    {
        title: '状态',
        dataIndex: 'type',
        valueEnum: WebSocketTypeDict
    },
    {title: '服务器', dataIndex: 'server'},
    {
        title: '类别',
        dataIndex: 'category',
        valueType: 'select',
        request: () => {
            return RequestGetDictList('request_category')
        }
    },
    {title: '连接时间', dataIndex: 'createTime', sorter: true, valueType: 'fromNow', hideInSearch: true},
    {
        title: '连接时间', dataIndex: 'createTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    beginCreateTime: value[0],
                    endCreateTime: value[1],
                } as SysRequestPageDTO
            }
        }
    },
    {
        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" className={"red3"} onClick={() => {
                execConfirm(() => {
                    return sysWebSocketRetreatByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定强退【id为${entity.id}】的数据吗？`)
            }}>强退</a>,
        ],
    },
];

export default TableColumnList
