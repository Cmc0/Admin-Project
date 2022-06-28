import {IMyTree, YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {ListToTree} from "../../../../util/TreeUtil";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {TreeSelect} from "antd";
import {SysAreaInsertOrUpdateDTO} from "@/api/SysAreaController";

export const InitForm: SysAreaInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (areaDictListRef: React.MutableRefObject<IMyTree[]>, currentForm: React.MutableRefObject<SysAreaInsertOrUpdateDTO>, deptDictListRef: React.MutableRefObject<IMyTree[]>): ProFormColumnsType<SysAreaInsertOrUpdateDTO>[] => {

    const newTreeList = ListToTree(
        areaDictListRef.current.filter(item =>
            item.id !== currentForm.current.id // 不要本节点
        ));

    return [
        {
            title: '上级区域', dataIndex: 'parentId', valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级区域',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
                options: newTreeList
            },
        },
        {
            title: '区域名', dataIndex: 'name',
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            }
        },
        {
            title: '关联部门',
            dataIndex: 'deptIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 2,
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_PARENT,
                options: deptDictListRef.current
            },
        },
        {
            title: '排序号',
            dataIndex: 'orderNo',
            valueType: 'digit',
            fieldProps: {className: 'w100', min: Number.MIN_SAFE_INTEGER}
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
