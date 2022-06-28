import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {FormInstance} from "antd/es";
import {SysParamInsertOrUpdateDTO} from "@/api/SysParamController";

export const InitForm: SysParamInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (useForm: FormInstance<SysParamInsertOrUpdateDTO>): ProFormColumnsType<SysParamInsertOrUpdateDTO>[] => {

    return [
        {
            title: '参数名', dataIndex: 'name', formItemProps: {
                rules: [
                    {required: true,},
                ],
            }
        },
        {
            title: '参数值', dataIndex: 'value', formItemProps: {
                rules: [
                    {required: true,},
                ],
            }
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
