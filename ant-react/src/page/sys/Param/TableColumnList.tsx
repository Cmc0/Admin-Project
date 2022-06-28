import {ActionType, ProColumns} from "@ant-design/pro-components";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {sysParamDeleteByIdSet, SysParamDO, SysParamInsertOrUpdateDTO} from "@/api/SysParamController";

const TableColumnList = (currentForm: React.MutableRefObject<SysParamInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysParamDO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {title: '参数名', dataIndex: 'name'},
    {title: '参数值', dataIndex: 'value', ellipsis: true},
    {title: '备注', dataIndex: 'remark'},
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },
    {
        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={() => {
                currentForm.current = {id: entity.id}
                setFormVisible(true)
            }}>编辑</a>,
            <a key="2" className={"red3"} onClick={() => {
                execConfirm(() => {
                    return sysParamDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除【${entity.name}】吗？`)
            }}>删除</a>,
        ],
    },
];

export default TableColumnList
