import {Button} from "antd";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import React, {useRef, useState} from "react";
import TableColumnList from "@/page/sysMonitor/OnlineUser/TableColumnList";
import {
    SysSysWebSocketPageVO,
    sysWebSocketPage,
    SysWebSocketPageDTO,
    sysWebSocketRetreatAll
} from "@/api/SysWebSocketController";
import {PoweroffOutlined} from "@ant-design/icons";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";

export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>(
        {});

    const actionRef = useRef<ActionType>(null)

    return (
        <ProTable<SysSysWebSocketPageVO, SysWebSocketPageDTO>
            actionRef={actionRef}
            rowKey={"id"}
            pagination={{
                showQuickJumper: true,
            }}
            columnEmptyText={false}
            columnsState={{
                value: columnsStateMap,
                onChange: setColumnsStateMap,
            }}
            revalidateOnFocus={false}
            columns={TableColumnList(actionRef)}
            options={{
                fullScreen: true,
            }}
            request={(params, sort, filter) => {
                return sysWebSocketPage({...params, enableFlag: true, sort})
            }}
            toolbar={{
                actions: [
                    <Button key={"1"} icon={<PoweroffOutlined/>} type="primary" danger onClick={() => {
                        execConfirm(() => {
                            return sysWebSocketRetreatAll().then(res => {
                                ToastSuccess(res.msg)
                                actionRef.current?.reload()
                            })
                        }, undefined, `确定【全部强退】吗？`)
                    }}>全部强退</Button>
                ],
            }}
        >
        </ProTable>
    )
}
