import {ActionType, ProColumns} from "@ant-design/pro-components";
import SysMenuDO from "@/model/entity/SysMenuDO";
import React from "react";
import {SysMenuInsertOrUpdateDTO} from "@/api/SysMenuController";
import {InDev} from "../../../../util/CommonUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysMenuInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysMenuDO>[] => [
    {title: 'id', dataIndex: 'socketId'},
    {
        title: '操作',
        width: 180,
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={InDev}>强退</a>,
        ],
    },
];

export default TableColumnList
