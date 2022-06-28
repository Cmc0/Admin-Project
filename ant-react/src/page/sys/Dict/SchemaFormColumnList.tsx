import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {SysDictInsertOrUpdateDTO} from "@/api/SysDictController";

export const InitForm: SysDictInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (): ProFormColumnsType<SysDictInsertOrUpdateDTO>[] => {

    return [
        {
            valueType: 'dependency',
            fieldProps: {
                name: ['type'],
            },
            columns: ({type}: SysDictInsertOrUpdateDTO): ProFormColumnsType<SysDictInsertOrUpdateDTO>[] => {
                return type === 1
                    ? [
                        {
                            title: '字典名',
                            dataIndex: 'name',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                    },
                                ],
                            }
                        },
                        {
                            title: '字典Key',
                            dataIndex: 'dictKey',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                    },
                                ],
                            }
                        },
                    ] : [
                        {
                            title: '字典项名',
                            dataIndex: 'name',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                    },
                                ],
                            }
                        },
                        {
                            title: '字典项Value',
                            dataIndex: 'value',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                    },
                                ],
                            }
                        },
                    ]
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
