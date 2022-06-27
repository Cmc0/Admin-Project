import React from "react";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {FormInstance} from "antd/es";
import {SysUserInsertOrUpdateDTO} from "@/api/SysUserController";
import {IMyOption, IMyTree, YesNoDict} from "../../../../util/DictUtil";
import {TreeSelect} from "antd";
import {randomNickname} from "../../../../util/UserUtil";

export const InitForm: SysUserInsertOrUpdateDTO = {
    enableFlag: true,
}

const SchemaFormColumnList = (useForm: FormInstance<SysUserInsertOrUpdateDTO>, deptDictListRef: React.MutableRefObject<IMyTree[]>, jobDictListRef: React.MutableRefObject<IMyTree[]>, roleDictListRef: React.MutableRefObject<IMyOption[]>): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {
    return [
        {
            dataIndex: 'nickname',
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
            title: (props, type, dom) => <>
                <span>用户昵称</span>
                <a className={"m-l-4"} onClick={() => {
                    useForm.setFieldsValue({nickname: randomNickname()})
                }}>随机</a>
            </>
        },
        {
            title: '邮箱',
            dataIndex: 'email',
        },
        {
            title: '关联角色',
            dataIndex: 'roleIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                options: roleDictListRef.current,
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
                showCheckedStrategy: TreeSelect.SHOW_ALL,
                // @ts-ignore
                options: deptDictListRef.current
            },
        },
        {
            title: '关联岗位',
            dataIndex: 'jobIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 2,
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_ALL,
                // @ts-ignore
                options: jobDictListRef.current
            },
        },
        {
            title: '正常/冻结',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },
        {
            title: '个人简介',
            dataIndex: 'bio',
            valueType: 'textarea',
            fieldProps: {
                showCount: true,
                maxLength: 100,
                allowClear: true,
            }
        },
    ]
}

export default SchemaFormColumnList
