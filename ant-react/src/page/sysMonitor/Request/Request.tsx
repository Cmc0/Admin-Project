import React, {useRef, useState} from "react";
import {ActionType, ProTable} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import moment from "moment";
import {Button} from "antd";
import {LoadingOutlined, ReloadOutlined} from "@ant-design/icons/lib";
import TableColumnList from "@/page/sysMonitor/Request/TableColumnList";
import {sysRequestPage, SysRequestPageDTO, SysRequestPageVO} from "@/api/SysRequestController";

export default function () {

    const actionRef = useRef<ActionType>(null)
    const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());
    const [polling, setPolling] = useState<number | undefined>(CommonConstant.POLLING_TIME);

    return (
        <ProTable<SysRequestPageVO, SysRequestPageDTO>
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
                return sysRequestPage({...params, sort})
            }}
            toolbar={{
                actions: [
                    <Button
                        key="1"
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
