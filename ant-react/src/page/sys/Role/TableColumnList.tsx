import {ActionType, ProColumns} from "@ant-design/pro-components";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {sysMenuDeleteByIdSet} from "@/api/SysMenuController";
import {SysRoleDO, SysRoleInsertOrUpdateDTO} from "@/api/SysRoleController";

const TableColumnList = (currentForm: React.MutableRefObject<SysRoleInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysRoleDO>[] => [
    {title: '角色名', dataIndex: 'name'},
    {title: '备注', dataIndex: 'remark'},
    {
        title: '默认角色',
        dataIndex: 'firstFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true
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
                    return sysMenuDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除【${entity.name}】吗？`)
            }}>删除</a>,
        ],
    },
];

export default TableColumnList
