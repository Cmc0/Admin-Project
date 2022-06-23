import {ActionType, ProColumns} from "@ant-design/pro-components";
import React from "react";
import {InDev} from "../../../../util/CommonUtil";
import {SysSysWebSocketPageVO} from "@/api/SysWebSocketController";

const TableColumnList = (actionRef: React.RefObject<ActionType>): ProColumns<SysSysWebSocketPageVO>[] => [
    {title: 'id', dataIndex: 'id'},
    {title: '用户名', dataIndex: 'userName'},
    {title: 'ip', dataIndex: 'ip'},
    {
        title: 'ip区域',
        dataIndex: 'region',
    },
    {title: '浏览器', dataIndex: 'browser'},
    {title: '操作系统', dataIndex: 'os'},
    {
        title: '移动端网页',
        dataIndex: 'mobileFlag',
    },
    {
        title: '状态',
        dataIndex: 'type',
    },
    {title: '服务器', dataIndex: 'server'},
    {
        title: '类别',
        dataIndex: 'category',
    },
    {title: '连接时间', dataIndex: 'createTime', sorter: true},
    {
        title: '操作',
        width: 180,
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={InDev}>强退</a>,
        ],
    },
];

export default TableColumnList
