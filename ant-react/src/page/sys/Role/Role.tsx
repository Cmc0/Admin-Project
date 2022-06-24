import React, {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Button} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {SysRoleDO, sysRolePage, SysRolePageDTO} from "@/api/SysRoleController";
import {SysMenuInsertOrUpdateDTO} from "@/api/SysMenuController";
import TableColumnList from "@/page/sys/Role/TableColumnList";

export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>(
        {
            defaultFlag: {show: false,},
        });

    const actionRef = useRef<ActionType>(null)

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<SysMenuInsertOrUpdateDTO>({})

    return (
        <ProTable<SysRoleDO, SysRolePageDTO>
            actionRef={actionRef}
            rowKey={"id"}
            pagination={{
                showQuickJumper: true,
                showSizeChanger: true,
            }}
            columnEmptyText={false}
            columnsState={{
                value: columnsStateMap,
                onChange: setColumnsStateMap,
            }}
            revalidateOnFocus={false}
            columns={TableColumnList(currentForm, setFormVisible, actionRef)}
            options={{
                fullScreen: true,
            }}
            request={(params, sort, filter) => {
                return sysRolePage({...params, enableFlag: true, sort})
            }}
            toolbar={{
                actions: [
                    <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {
                        currentForm.current = {}
                        setFormVisible(true)
                    }}>新建</Button>
                ],
            }}
        >
        </ProTable>
    )
}
