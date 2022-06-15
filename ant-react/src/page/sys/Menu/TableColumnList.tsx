import {ProColumns} from "@ant-design/pro-components";
import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {Space} from "antd";
import {HomeFilled} from "@ant-design/icons/lib";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {RouterDict} from "@/router/RouterMap";
import {YesNoEnum} from "../../../../util/DictUtil";
import React from "react";

const TableColumnList: ProColumns<BaseMenuDO>[] = [
    {
        title: '菜单名',
        dataIndex: 'name',
        render: (dom, entity) => {
            return (
                <Space className="ai-c">
                    {entity.firstFlag && <HomeFilled className="cyan1" title="起始页面"/>}
                    {entity.icon && <MyIcon icon={entity.icon}/>}
                    {entity.parentId + '' === '0' ? <strong>{dom}</strong> : dom}
                </Space>
            )
        },
    },
    {title: '路径', dataIndex: 'path'},
    {title: '权限', dataIndex: 'auths'},
    {
        title: '路由',
        dataIndex: 'router',
        valueType: 'select',
        request: async () => {
            return RouterDict
        }
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
    {
        title: '起始页面',
        dataIndex: 'firstFlag',
        valueEnum: YesNoEnum,
    },
    {
        title: '权限菜单',
        dataIndex: 'authFlag',
        valueEnum: YesNoEnum,
    },
    {
        title: '外链',
        dataIndex: 'linkFlag',
        valueEnum: YesNoEnum,
    },
    {
        title: '显示',
        dataIndex: 'showFlag',
        valueEnum: YesNoEnum
    },
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoEnum
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true
    },
];

export default TableColumnList
