import React from "react";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {FormInstance} from "antd/es";
import {SysUserInsertOrUpdateDTO} from "@/api/SysUserController";
import {YesNoDict} from "../../../../util/DictUtil";

export const InitForm: SysUserInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (useForm: FormInstance<SysUserInsertOrUpdateDTO>): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {
    return [
        {
            title: '正常/冻结',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },
    ]
}

export default SchemaFormColumnList
