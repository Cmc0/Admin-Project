import {ActionType, ProColumns} from "@ant-design/pro-components";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {SysRoleDO, SysRoleInsertOrUpdateDTO} from "@/api/SysRoleController";
import {InDev} from "../../../../util/CommonUtil";
import {SysRequestPageDTO} from "@/api/SysRequestController";
import {Dropdown, Menu} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";

const TableColumnList = (currentForm: React.MutableRefObject<SysRoleInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysRoleDO>[] => [
    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
    },
    {
        title: '头像', dataIndex: 'avatarUrl', valueType: 'avatar', hideInSearch: true,
    },
    {title: '昵称', dataIndex: 'nickname'},
    {
        title: '邮箱',
        dataIndex: 'email',
    },
    {
        title: '账号正常',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
    },
    {
        title: '创建时间', dataIndex: 'createTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    beginCreateTime: value[0],
                    endCreateTime: value[1],
                } as SysRequestPageDTO
            }
        }
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
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
            <a key="2" className={"red3"} onClick={InDev}>注销</a>,
            ,
            <Dropdown key="3" overlay={<Menu items={[
                {
                    key: '1',
                    label: <a onClick={InDev}>重置头像</a>,
                },
                {
                    key: '2',
                    label: <a onClick={InDev}>修改密码</a>
                },
            ]}>
            </Menu>}>
                <a><EllipsisOutlined/></a>
            </Dropdown>,
        ],
    },
];

export default TableColumnList
