import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {FormInstance} from "antd/es";
import {SysRoleInsertOrUpdateDTO} from "@/api/SysRoleController";
import {TreeSelect} from "antd";
import {RequestOptionsType} from "@ant-design/pro-components";

export const InitForm: SysRoleInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (useForm: FormInstance<SysRoleInsertOrUpdateDTO>, menuDictListRef: React.MutableRefObject<RequestOptionsType[]>): ProFormColumnsType<SysRoleInsertOrUpdateDTO>[] => {

    return [
        {
            title: '角色名', dataIndex: 'name', formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            }
        },
        {
            title: '关联菜单',
            dataIndex: 'menuIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 2,
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_PARENT,
                options: menuDictListRef.current
            },
        },
        {
            title: '默认角色',
            dataIndex: 'defaultFlag',
            valueEnum: YesNoDict,
            tooltip: '每个用户都拥有此角色权限，备注：只会有一个默认角色',
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
