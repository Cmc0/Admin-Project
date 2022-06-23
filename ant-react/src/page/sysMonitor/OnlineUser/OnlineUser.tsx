import {Button} from "antd";
import {CloseOutlined} from "@ant-design/icons/lib";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import React, {useRef, useState} from "react";
import TableColumnList from "@/page/sysMonitor/OnlineUser/TableColumnList";
import {InDev} from "../../../../util/CommonUtil";
import {SysSysWebSocketPageVO, sysWebSocketPage, SysWebSocketPageDTO} from "@/api/SysWebSocketController";

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
                    <Button key={"1"} icon={<CloseOutlined/>} type="primary" danger onClick={InDev}>全部强退</Button>
                ],
            }}
        >
        </ProTable>
    )
}
