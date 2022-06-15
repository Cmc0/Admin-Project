import {RouterDict} from "@/router/RouterMap";
import {YesNoEnum} from "../../../../util/DictUtil";
import React from "react";
import {MenuInsertOrUpdateDTO} from "@/api/MenuController";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {FlatTree, ListToTree} from "../../../../util/TreeUtil";

const SchemaFormColumnList: (treeList: BaseMenuDO[], ignoreId?: number) => ProFormColumnsType<MenuInsertOrUpdateDTO>[] = (treeList: BaseMenuDO[], ignoreId?: number) => {

    // 先扁平化树结构
    const list = FlatTree(
        treeList,
        true,
        (item) => item.id !== ignoreId // 不要本节点
    ).map((item) => ({
        id: item.id,
        key: item.id,
        value: item.id,
        title: item.name,
        parentId: item.parentId,
    }))

    return [
        {
            title: '上级菜单', dataIndex: 'parentId', valueType: "treeSelect",
            request: async () => {
                return ListToTree(list)
            }
        },
        {title: '菜单名', dataIndex: 'name'},
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
        {title: '排序号', dataIndex: 'orderNo'},
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
    ]
}

export default SchemaFormColumnList
