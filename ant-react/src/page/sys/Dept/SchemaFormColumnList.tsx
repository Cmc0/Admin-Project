import {IMyOption, IMyTree, YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {ListToTree} from "../../../../util/TreeUtil";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {SysDeptInsertOrUpdateDTO} from "@/api/SysDeptController";
import {TreeSelect} from "antd";

export const InitForm: SysDeptInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (deptDictListRef: React.MutableRefObject<IMyTree[]>, currentForm: React.MutableRefObject<SysDeptInsertOrUpdateDTO>, areaDictListRef: React.MutableRefObject<IMyTree[]>, userDictListRef: React.MutableRefObject<IMyOption[]>): ProFormColumnsType<SysDeptInsertOrUpdateDTO>[] => {

    const newTreeList = ListToTree(
        deptDictListRef.current.filter(item =>
            item.id !== currentForm.current.id // 不要本节点
        ));

    return [
        {
            title: '上级部门', dataIndex: 'parentId', valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级部门',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
                options: newTreeList
            },
        },
        {
            title: '部门名', dataIndex: 'name',
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            }
        },
        {
            title: '关联用户',
            dataIndex: 'userIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: "multiple",
                // @ts-ignore
                options: userDictListRef.current,
            }
        },
        {
            title: '关联区域',
            dataIndex: 'areaIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 2,
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_PARENT,
                options: areaDictListRef.current
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
