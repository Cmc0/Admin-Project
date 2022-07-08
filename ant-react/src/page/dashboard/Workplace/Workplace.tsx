import {RouteContext, RouteContextType, StatisticCard} from "@ant-design/pro-components";
import {Divider, Statistic} from "antd";
import {useEffect, useState} from "react";
import {serverWorkInfo, ServerWorkInfoVO} from "@/api/ServerController";

export default function () {

    const [serverInfo, setServerInfo] = useState<ServerWorkInfoVO>({});

    function doSetServerInfo() {
        serverWorkInfo().then(res => {
            setServerInfo(res.data)
        })
    }

    useEffect(() => {
        doSetServerInfo()
        const serverInfoInterval = setInterval(doSetServerInfo, 20 * 1000);
        return () => {
            clearInterval(serverInfoInterval)
        }
    }, [])

    return (
        <RouteContext.Consumer>
            {(value: RouteContextType) => {
                return <>
                    <StatisticCard.Group direction={value.isMobile ? 'column' : 'row'}>
                        <StatisticCard
                            statistic={{
                                title: 'JVM内存使用',
                                value: (getNumber(serverInfo.jvmUsedMemory)),
                                description: <Statistic title={getTitle(serverInfo.jvmTotalMemory)}
                                                        value={getPercentage(serverInfo.jvmTotalMemory, serverInfo.jvmUsedMemory)}/>,
                            }}
                            chart={
                                <img
                                    src="https://gw.alipayobjects.com/zos/alicdn/ShNDpDTik/huan.svg"
                                    alt="百分比"
                                    width="100%"
                                />
                            }
                            chartPlacement="left"
                        />
                        <Divider className={"h100"} type={value.isMobile ? 'horizontal' : 'vertical'}/>
                        <StatisticCard
                            statistic={{
                                title: '系统内存使用',
                                value: (getNumber(serverInfo.memoryUsed)),
                                description: <Statistic title={getTitle(serverInfo.memoryTotal)}
                                                        value={getPercentage(serverInfo.memoryTotal, serverInfo.memoryUsed)}/>,
                            }}
                            chart={
                                <img
                                    src="https://gw.alipayobjects.com/zos/alicdn/ShNDpDTik/huan.svg"
                                    alt="百分比"
                                    width="100%"
                                />
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard
                            statistic={{
                                title: 'CPU使用',
                                value: (serverInfo.cpuUsed || 0),
                                description: <Statistic title="占比 100"
                                                        value={getPercentage(100, serverInfo.cpuUsed)}/>,
                            }}
                            chart={
                                <img
                                    src="https://gw.alipayobjects.com/zos/alicdn/ShNDpDTik/huan.svg"
                                    alt="百分比"
                                    width="100%"
                                />
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard
                            statistic={{
                                title: '磁盘使用',
                                value: (getNumber(serverInfo.diskUsed)),
                                description: <Statistic title={getTitle(serverInfo.diskTotal)}
                                                        value={getPercentage(serverInfo.diskTotal, serverInfo.diskUsed)}/>,
                            }}
                            chart={
                                <img
                                    src="https://gw.alipayobjects.com/zos/alicdn/ShNDpDTik/huan.svg"
                                    alt="百分比"
                                    width="100%"
                                />
                            }
                            chartPlacement="left"
                        />
                    </StatisticCard.Group>
                </>
            }}
        </RouteContext.Consumer>
    )
}

// 通过 byte获取 mb的字符串
function getNumber(number: number = 0) {
    if (number > 1024 * 1024 * 1024) {
        return Math.round((number / 1024 / 1024 / 1024) * 100) / 100 + 'G'
    }
    return Math.round((number / 1024 / 1024) * 100) / 100 + 'MB'
}

// 获取：占比
function getTitle(total: number = 0) {
    return '占比 ' + getNumber(total)
}

// 获取：百分比
function getPercentage(total: number = 0, value: number = 0) {
    if (total === 0 && value === 0) {
        return '0%'
    }
    return Math.round((value / total) * 10000) / 100 + '%'
}
