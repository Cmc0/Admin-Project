import {ProColumns, TableDropdown} from "@ant-design/pro-components";
import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {Space} from "antd";
import {HomeFilled} from "@ant-design/icons/lib";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {RouterMapKeyList} from "@/router/RouterMap";
import {YesNoDict} from "../../../../util/DictUtil";
import React, {Dispatch, SetStateAction} from "react";

const TableColumnList = (id: React.MutableRefObject<number>, setFormVisible: Dispatch<SetStateAction<boolean>>): ProColumns<BaseMenuDO>[] => [
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
        fieldProps: {
            showSearch: true,
            options: RouterMapKeyList,
        }
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
    {
        title: '起始页面',
        dataIndex: 'firstFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '外链',
        dataIndex: 'linkFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '显示',
        dataIndex: 'showFlag',
        valueEnum: YesNoDict
    },
    {
        title: '权限菜单',
        dataIndex: 'authFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true
    },
    {
        title: '操作',
        width: 160,
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="view">查看</a>,
            <a key="edit" onClick={() => {
                if (entity.id) {
                    id.current = entity.id
                    setFormVisible(true)
                }
            }}>编辑</a>,
            <TableDropdown
                key="actionGroup"
                menus={[
                    {key: 'del', name: '删除'},
                ]}
                onSelect={(key) => {
                    console.log(key)
                }}
            />,
        ],
    },
];

export default TableColumnList
