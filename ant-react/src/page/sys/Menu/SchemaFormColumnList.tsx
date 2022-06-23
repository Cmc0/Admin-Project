import {RouterMapKeyList} from "@/router/RouterMap";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import SysMenuDO from "@/model/entity/SysMenuDO";
import {FlatTree, ListToTree} from "../../../../util/TreeUtil";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {FormInstance} from "antd/es";
import {SysMenuInsertOrUpdateDTO} from "@/api/SysMenuController";
import MyIcon, {IconList} from "@/componse/MyIcon/MyIcon";
import {Space} from "antd";
import {RequestOptionsType} from "@ant-design/pro-utils/lib/typing";

export const InitForm = {
    enableFlag: true,
    showFlag: true,
}

const SchemaFormColumnList = (treeList: SysMenuDO[], useForm: FormInstance<SysMenuInsertOrUpdateDTO>, ignoreId?: number): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {

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
            fieldProps: {
                placeholder: '为空则表示顶级菜单',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
                options: newTreeList
            },
        },
        {
            title: '菜单名', dataIndex: 'name', formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            }
        },
        {
            title: '排序号',
            dataIndex: 'orderNo',
            valueType: 'digit',
            fieldProps: {className: 'w100', min: Number.MIN_SAFE_INTEGER}
        },
        {
            valueType: 'dependency',
            fieldProps: {
                name: ['authFlag'],
            },
            columns: ({authFlag}: SysMenuInsertOrUpdateDTO): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {
                return authFlag
                    ? [
                        {
                            dataIndex: 'auths',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                        message: '请输入权限'
                                    },
                                ],
                            },
                            valueType: 'textarea',
                            fieldProps: {
                                showCount: true,
                                maxLength: 255,
                                allowClear: true,
                            },
                            title: (props, type, dom) => <>
                                <span>权限</span>
                                <a className={"m-l-4"} onClick={() => {
                                    useForm.setFieldsValue({auths: 'menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById'})
                                }}>生成示例</a>
                            </>
                        }
                    ]
                    : [
                        {
                            dataIndex: 'path',
                            title: (props, type, dom) => <>
                                <span>路径</span>
                                <a className={"m-l-4"} onClick={() => {
                                    useForm.setFieldsValue({path: '/main/sys/menu'})
                                }}>生成示例</a>
                            </>,
                        },
                        {
                            title: '图标',
                            dataIndex: 'icon',
                            valueType: 'select',
                            fieldProps: {
                                showSearch: true,
                                options: IconList,
                                optionItemRender: (item: RequestOptionsType) => {
                                    return <Space key={item.value}>
                                        <MyIcon icon={item.value ? item.value as string : undefined}/> {item.value}
                                    </Space>
                                },
                            }
                        },
                        {
                            title: '路由',
                            dataIndex: 'router',
                            valueType: 'select',
                            fieldProps: {
                                showSearch: true,
                                options: RouterMapKeyList,
                            }
                        },
                        {
                            title: '起始页面',
                            dataIndex: 'firstFlag',
                            valueEnum: YesNoDict,
                            tooltip: '是否为默认打开的页面',
                            valueType: 'switch',
                        },
                        {
                            valueType: 'dependency',
                            fieldProps: {
                                name: ['path'],
                            },
                            columns: ({path}: SysMenuInsertOrUpdateDTO): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {
                                return [
                                    {
                                        title: '外链',
                                        dataIndex: 'linkFlag',
                                        valueEnum: YesNoDict,
                                        fieldProps: {disabled: path?.startsWith("http")},
                                        tooltip: '如果开启，打开页面时，会在一个新的窗口打开此页面，可以配合 router',
                                        valueType: 'switch',
                                    },
                                ]
                            }
                        },
                        {
                            title: '显示',
                            dataIndex: 'showFlag',
                            valueEnum: YesNoDict,
                            tooltip: '是否在左侧菜单栏显示',
                            valueType: 'switch',
                        },
                    ];
            },
        },
        {
            title: '权限菜单',
            dataIndex: 'authFlag',
            valueEnum: YesNoDict,
            tooltip: '不显示，只代表菜单权限',
            valueType: 'switch',
        },
        {
            title: '启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },
        {
            title: '备注',
            dataIndex: 'remark',
            valueType: 'textarea',
            fieldProps: {
                showCount: true,
                maxLength: 300,
                allowClear: true,
            }
        }
    ]
}

export default SchemaFormColumnList
