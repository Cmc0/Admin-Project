import {IMyOption, IMyTree, YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {ListToTree} from "../../../../util/TreeUtil";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {SysJobInsertOrUpdateDTO} from "@/api/SysJobController";

export const InitForm: SysJobInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (jobDictListRef: React.MutableRefObject<IMyTree[]>, currentForm: React.MutableRefObject<SysJobInsertOrUpdateDTO>, userDictListRef: React.MutableRefObject<IMyOption[]>): ProFormColumnsType<SysJobInsertOrUpdateDTO>[] => {
    return [
        {
            title: '上级岗位', dataIndex: 'parentId', valueType: "treeSelect",
            request: async () => {
                return ListToTree(
                    jobDictListRef.current.filter(item =>
                        item.id !== currentForm.current.id // 不要本节点
                    ));
            },
            fieldProps: {
                placeholder: '为空则表示顶级岗位',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
        },
        {
            title: '岗位名', dataIndex: 'name',
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
                maxTagCount: 'responsive',
                // @ts-ignore
                options: userDictListRef.current,
            }
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
