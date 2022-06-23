import {ActionType, ProColumns} from "@ant-design/pro-components";
import React from "react";
import {SysSysWebSocketPageVO, sysWebSocketRetreatByIdSet} from "@/api/SysWebSocketController";
import {handlerRegion} from "../../../../util/StrUtil";
import {RequestGetDictList, WebSocketTypeDict, YesNoBaseDict} from "../../../../util/DictUtil";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";

const TableColumnList = (actionRef: React.RefObject<ActionType>): ProColumns<SysSysWebSocketPageVO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {title: 'id', dataIndex: 'id'},
    {title: '用户名', dataIndex: 'userName'},
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
    {title: '连接时间', dataIndex: 'createTime', sorter: true, valueType: 'fromNow'},
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
                }, undefined, `确定强退【${entity.userName}】吗？`)
            }}>强退</a>,
        ],
    },
];

export default TableColumnList
