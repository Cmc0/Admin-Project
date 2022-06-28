import {ActionType, ProColumns} from "@ant-design/pro-components";
import {Dropdown, Menu} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {CalcOrderNo} from "../../../../util/TreeUtil";
import {sysAreaDeleteByIdSet, SysAreaDO, SysAreaInsertOrUpdateDTO} from "@/api/SysAreaController";

const TableColumnList = (currentForm: React.MutableRefObject<SysAreaInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysAreaDO>[] => [
    {
        title: '区域名',
        dataIndex: 'name',
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
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
        width: 180,
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={() => {
                currentForm.current = {id: entity.id}
                setFormVisible(true)
            }}>编辑</a>,
            <a key="2" className={"red3"} onClick={() => {
                execConfirm(() => {
                    return sysAreaDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除【${entity.name}】吗？`)
            }}>删除</a>,
            <Dropdown key="3" overlay={<Menu items={[
                {
                    key: '1',
                    label: <a onClick={() => {
                        currentForm.current = {parentId: entity.id}
                        CalcOrderNo(currentForm.current, entity)
                        setFormVisible(true)
                    }}>
                        添加下级
                    </a>,
                },
            ]}>
            </Menu>}>
                <a><EllipsisOutlined/></a>
            </Dropdown>,
        ],
    },
];

export default TableColumnList
