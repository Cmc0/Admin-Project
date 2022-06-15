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

    const newTreeList = ListToTree(list);

    return [
        {
            title: '上级菜单', dataIndex: 'parentId', valueType: "treeSelect",
            request: async () => {
                return newTreeList
            },
            fieldProps: {
                placeholder: '为空则表示顶级菜单',
                allowClear: true
            }
        },
        {title: '菜单名', dataIndex: 'name', formItemProps: {required: true}},
        {title: '排序号', dataIndex: 'orderNo', valueType: 'digit', fieldProps: {className: 'w100', allowClear: true}},
        {
            valueType: 'dependency',
            fieldProps: {
                name: ['authFlag'],
            },
            columns: ({authFlag}) => {
                return authFlag === 'true'
                    ? [
                        {
                            title: '权限', dataIndex: 'auths',
                            formItemProps: {required: true},
                            valueType: 'textarea'
                        }
                    ]
                    : [
                        {title: '路径', dataIndex: 'path'},
                        {
                            title: '路由',
                            dataIndex: 'router',
                            valueType: 'select',
                            request: async () => {
                                return RouterDict
                            }
                        },
                        {
                            title: '起始页面',
                            dataIndex: 'firstFlag',
                            valueEnum: YesNoEnum,
                            fieldProps: {allowClear: false},
                            tooltip: '是否为默认打开的页面',
                        },
                        {
                            title: '外链',
                            dataIndex: 'linkFlag',
                            valueEnum: YesNoEnum,
                            fieldProps: {allowClear: false},
                            tooltip: '如果开启，打开页面时，会在一个新的窗口打开此页面，可以配合 router',
                        },
                        {
                            title: '显示',
                            dataIndex: 'showFlag',
                            valueEnum: YesNoEnum,
                            fieldProps: {allowClear: false},
                            tooltip: '是否在左侧菜单栏显示',
                        },
                    ];
            },
        },
        {
            title: '权限菜单',
            dataIndex: 'authFlag',
            valueEnum: YesNoEnum,
            fieldProps: {allowClear: false},
        },
        {
            title: '启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoEnum,
            fieldProps: {allowClear: false},
        },
        {
            title: '备注',
            dataIndex: 'remark',
            valueType: 'textarea'
        }
    ]
}

export default SchemaFormColumnList
