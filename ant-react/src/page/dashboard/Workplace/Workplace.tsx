import {RouteContext, RouteContextType, StatisticCard} from "@ant-design/pro-components";
import {Statistic} from "antd";
import {useEffect, useRef, useState} from "react";
import {serverWorkInfo, ServerWorkInfoVO} from "@/api/ServerController";
import * as echarts from "echarts";

const WorkplaceJvmECharts = "WorkplaceJvmECharts"
const WorkplaceMemoryECharts = "WorkplaceMemoryECharts"
const WorkplaceCpuECharts = "WorkplaceCpuECharts"
const WorkplaceDiskECharts = "WorkplaceDiskECharts"

const CpuTotal = 100

function setEChartsOption(eachChartsRef: React.MutableRefObject<echarts.EChartsType | undefined>, freeTotal: number | undefined, usedTotal: number | undefined) {

    eachChartsRef.current?.hideLoading()

    eachChartsRef.current?.setOption({
        series: [
            {
                type: 'pie',
                hoverAnimation: false,
                radius: ['50%', '70%'],
                label: {
                    show: false,
                },
                data: [
                    {value: usedTotal, itemStyle: {color: '#6395F9'}},
                    {value: freeTotal, itemStyle: {color: '#F0F0F0'}},
                ]
            }
        ]
    })

}

export default function () {

    const [serverInfo, setServerInfo] = useState<ServerWorkInfoVO>({});
    const workplaceJvmEChartsRef = useRef<echarts.EChartsType>()
    const workplaceMemoryEChartsRef = useRef<echarts.EChartsType>()
    const workplaceCpuEChartsRef = useRef<echarts.EChartsType>()
    const workplaceDiskEChartsRef = useRef<echarts.EChartsType>()

    function doSetServerInfo() {
        serverWorkInfo().then(res => {
            setServerInfo(res.data)
            setEChartsOption(workplaceJvmEChartsRef, res.data.jvmFreeMemory, res.data.jvmUsedMemory)
            setEChartsOption(workplaceMemoryEChartsRef, res.data.memoryAvailable, res.data.memoryUsed)
            setEChartsOption(workplaceCpuEChartsRef, (CpuTotal - res.data.cpuUsed!), res.data.cpuUsed)
            setEChartsOption(workplaceDiskEChartsRef, res.data.diskUsable, res.data.diskUsed)
        })
    }

    useEffect(() => {
        if (workplaceJvmEChartsRef.current) {
            workplaceJvmEChartsRef.current.dispose()
        }
        if (workplaceMemoryEChartsRef.current) {
            workplaceMemoryEChartsRef.current.dispose()
        }
        if (workplaceCpuEChartsRef.current) {
            workplaceCpuEChartsRef.current.dispose()
        }
        if (workplaceDiskEChartsRef.current) {
            workplaceDiskEChartsRef.current.dispose()
        }
        workplaceJvmEChartsRef.current = echarts.init(document.getElementById(WorkplaceJvmECharts)!)
        workplaceMemoryEChartsRef.current = echarts.init(document.getElementById(WorkplaceMemoryECharts)!)
        workplaceCpuEChartsRef.current = echarts.init(document.getElementById(WorkplaceCpuECharts)!)
        workplaceDiskEChartsRef.current = echarts.init(document.getElementById(WorkplaceDiskECharts)!)

        workplaceJvmEChartsRef.current?.showLoading()
        workplaceMemoryEChartsRef.current?.showLoading()
        workplaceCpuEChartsRef.current?.showLoading()
        workplaceDiskEChartsRef.current?.showLoading()
        doSetServerInfo()
        const serverInfoInterval = setInterval(doSetServerInfo, 15 * 1000);
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
                                <div id={WorkplaceJvmECharts} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard
                            statistic={{
                                title: '系统内存使用',
                                value: (getNumber(serverInfo.memoryUsed)),
                                description: <Statistic title={getTitle(serverInfo.memoryTotal)}
                                                        value={getPercentage(serverInfo.memoryTotal, serverInfo.memoryUsed)}/>,
                            }}
                            chart={
                                <div id={WorkplaceMemoryECharts} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard
                            statistic={{
                                title: 'CPU使用',
                                value: (serverInfo.cpuUsed || 0),
                                description: <Statistic title={`占比 ${CpuTotal}`}
                                                        value={getPercentage(CpuTotal, serverInfo.cpuUsed)}/>,
                            }}
                            chart={
                                <div id={WorkplaceCpuECharts} className={"w-100 h-100"}/>
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
                                <div id={WorkplaceDiskECharts} className={"w-100 h-100"}/>
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
