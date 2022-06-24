import React, {useRef, useState} from "react";
import {ActionType, ProTable} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import moment from "moment";
import {Badge, Button, Space, Typography} from "antd";
import {LoadingOutlined, ReloadOutlined} from "@ant-design/icons/lib";
import TableColumnList from "@/page/sysMonitor/Request/TableColumnList";
import {
    sysRequestAllAvgPro,
    SysRequestAllAvgVO,
    sysRequestPage,
    SysRequestPageDTO,
    SysRequestPageVO
} from "@/api/SysRequestController";

export default function () {

    const actionRef = useRef<ActionType>(null)
    const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());
    const [polling, setPolling] = useState<number | undefined>(CommonConstant.POLLING_TIME)
    const [sysRequestAllAvgVO, setSysRequestAllAvgVO] = useState<SysRequestAllAvgVO>({avg: 0, count: 0})

    return (
        <ProTable<SysRequestPageVO, SysRequestPageDTO>
            actionRef={actionRef}
            rowKey={"id"}
            pagination={{
                showQuickJumper: true,
                showSizeChanger: true,
            }}
            headerTitle={<Space size={16}>
                <span>上次更新时间：{moment(lastUpdateTime).format('HH:mm:ss')}</span>
                <div className={"hand"} title={`筛选条件，接口平均响应耗时，共请求 ${sysRequestAllAvgVO.count}次`}>
                    <Badge status="processing"
                           text={
                               <Typography.Text
                                   strong
                                   type={sysRequestAllAvgVO.avg! < 800 ? 'success' : (sysRequestAllAvgVO.avg! > 1600 ? 'danger' : 'warning')}>
                                   avg：{sysRequestAllAvgVO.avg}ms
                               </Typography.Text>
                           }/>
                </div>
            </Space>}
            polling={polling}
            columnEmptyText={false}
            revalidateOnFocus={false}
            columns={TableColumnList(actionRef)}
            options={{
                fullScreen: true,
            }}
            request={(params, sort, filter) => {
                setLastUpdateTime(new Date())
                sysRequestAllAvgPro({...params}).then(res => {
                    setSysRequestAllAvgVO(res.data)
                })
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
