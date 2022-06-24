import React, {useRef, useState} from "react";
import {ActionType, ProTable} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import {sysWebSocketPage, sysWebSocketRetreatAll} from "@/api/SysWebSocketController";
import moment from "moment";
import TableColumnList from "@/page/sysMonitor/OnlineUser/TableColumnList";
import {Button} from "antd";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {LoadingOutlined, PoweroffOutlined, ReloadOutlined} from "@ant-design/icons/lib";
import {SysRoleDO, SysRolePageDTO} from "@/api/SysRoleController";

export default function () {

    const actionRef = useRef<ActionType>(null)
    const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());
    const [polling, setPolling] = useState<number | undefined>(CommonConstant.POLLING_TIME);

    return (
        <ProTable<SysRoleDO, SysRolePageDTO>
            actionRef={actionRef}
            rowKey={"id"}
            pagination={{
                showQuickJumper: true,
                showSizeChanger: true,
            }}
            headerTitle={`上次更新时间：${moment(lastUpdateTime).format('HH:mm:ss')}`}
            polling={polling}
            columnEmptyText={false}
            revalidateOnFocus={false}
            columns={TableColumnList(actionRef)}
            options={{
                fullScreen: true,
            }}
            request={(params, sort, filter) => {
                setLastUpdateTime(new Date())
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
                    }}>全部强退</Button>,
                    <Button
                        key="2"
                        type="primary"
                        onClick={() => {
                            if (polling) {
                                setPolling(undefined);
                                return;
                            }
                            setPolling(CommonConstant.POLLING_TIME);
                        }}
                    >
                        {polling ? <LoadingOutlined/> : <ReloadOutlined/>}
                        {polling ? '停止轮询' : '开始轮询'}
                    </Button>,
                ],
            }}
        >
        </ProTable>
    )
}
