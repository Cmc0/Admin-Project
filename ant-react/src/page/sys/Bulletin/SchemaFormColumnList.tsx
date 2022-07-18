import React from "react";
import {ProFormColumnsType} from "@ant-design/pro-form/lib/components/SchemaForm/typing";
import {SysBulletinInsertOrUpdateDTO} from "@/api/SysBulletinController";
import DictLongListVO from "@/model/vo/DictLongListVO";

export const InitForm: SysBulletinInsertOrUpdateDTO = {}

const SchemaFormColumnList = (bulletinTypeDictListRef: React.MutableRefObject<DictLongListVO[]>): ProFormColumnsType<SysBulletinInsertOrUpdateDTO>[] => {

    return [
        {
            title: '公告类型', dataIndex: 'type', formItemProps: {
                rules: [
                    {required: true,},
                ],
            },
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                options: bulletinTypeDictListRef.current,
            }
        },
        {
            title: '标题', dataIndex: 'title', formItemProps: {
                rules: [
                    {required: true,},
                ],
            }
        },
        {
            title: '发布时间',
            dataIndex: 'publishTime',
            valueType: 'dateTime',
            fieldProps: {
                className: "w100",
            },
            formItemProps: {
                rules: [
                    {required: true,},
                ],
            }
        },
        {
            title: '公告内容',
            dataIndex: 'content',
            valueType: 'textarea',
            fieldProps: {
                showCount: true,
                allowClear: true,
            },
            formItemProps: {
                rules: [
                    {required: true,},
                ],
            }
        },
        {
            title: '备注',
            dataIndex: 'remark',
            valueType: 'textarea',
            fieldProps: {
                showCount: true,
                maxLength: 300,
                allowClear: true,
            },
        }
    ]
}

export default SchemaFormColumnList
