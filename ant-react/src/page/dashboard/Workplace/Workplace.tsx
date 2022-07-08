import {ProCard, RouteContext, RouteContextType, StatisticCard} from "@ant-design/pro-components";
import {useEffect, useRef, useState} from "react";
import {serverWorkInfo, ServerWorkInfoVO} from "@/api/ServerController";
import * as echarts from "echarts";
import moment from "moment";
import {
    systemAnalyzeActiveUser,
    systemAnalyzeActiveUserTrend,
    SystemAnalyzeActiveUserTrendVO,
    SystemAnalyzeActiveUserVO,
    systemAnalyzeTrafficUsage,
    SystemAnalyzeTrafficUsageVO,
    systemAnalyzeUser,
    SystemAnalyzeUserVO
} from "@/api/SystemAnalyzeController";

const WorkplaceJvmECharts = "WorkplaceJvmECharts"
const WorkplaceMemoryECharts = "WorkplaceMemoryECharts"
const WorkplaceCpuECharts = "WorkplaceCpuECharts"
const WorkplaceDiskECharts = "WorkplaceDiskECharts"

const ActiveUserTrendECharts = "ActiveUserTrendECharts"
const TrafficUsageECharts = "TrafficUsageECharts"

const CpuTotal = 100

function setEChartsOption(eachChartsRef: React.MutableRefObject<echarts.EChartsType | undefined>, freeTotal: number | undefined, usedTotal: number | undefined, total: number | undefined) {

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
                    {value: usedTotal, itemStyle: {color: getDangerFlag(total, usedTotal) ? '#cf1322' : '#6395F9'}},
                    {value: freeTotal, itemStyle: {color: '#F0F0F0'}},
                ]
            }
        ]
    })

}

export default function () {

    const [serverInfo, setServerInfo] = useState<ServerWorkInfoVO>({});
    const [activeUser, setActiveUser] = useState<SystemAnalyzeActiveUserVO>({});
    const [activeUserTrendList, setActiveUserTrendList] = useState<SystemAnalyzeActiveUserTrendVO[] | undefined>([]);
    const [trafficUsage, setTrafficUsage] = useState<SystemAnalyzeTrafficUsageVO>({});
    const [analyzeUser, setAnalyzeUser] = useState<SystemAnalyzeUserVO>({});

    const workplaceJvmEChartsRef = useRef<echarts.EChartsType>()
    const workplaceMemoryEChartsRef = useRef<echarts.EChartsType>()
    const workplaceCpuEChartsRef = useRef<echarts.EChartsType>()
    const workplaceDiskEChartsRef = useRef<echarts.EChartsType>()

    function doSetServerInfo() {
        serverWorkInfo().then(res => {
            setServerInfo(res.data)
            setEChartsOption(workplaceJvmEChartsRef, res.data.jvmFreeMemory, res.data.jvmUsedMemory, res.data.jvmTotalMemory)
            setEChartsOption(workplaceMemoryEChartsRef, res.data.memoryAvailable, res.data.memoryUsed, res.data.memoryTotal)
            setEChartsOption(workplaceCpuEChartsRef, (CpuTotal - res.data.cpuUsed!), res.data.cpuUsed, CpuTotal)
            setEChartsOption(workplaceDiskEChartsRef, res.data.diskUsable, res.data.diskUsed, res.data.diskTotal)
        })
    }

    function doSystemAnalyzeActiveUser() {
        systemAnalyzeActiveUser().then(res => {
            setActiveUser(res.data)
        })
    }

    function doSystemAnalyzeActiveUserTrend() {
        systemAnalyzeActiveUserTrend().then(res => {
            setActiveUserTrendList(res.data)
        })
    }

    function doSystemAnalyzeTrafficUsage() {
        systemAnalyzeTrafficUsage().then(res => {
            setTrafficUsage(res.data)
        })
    }

    function doSystemAnalyzeUser() {
        systemAnalyzeUser().then(res => {
            setAnalyzeUser(res.data)
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

        doSystemAnalyzeActiveUser()
        doSystemAnalyzeActiveUserTrend()
        doSystemAnalyzeTrafficUsage()
        doSystemAnalyzeUser()

        const systemAnalyzeInterval = setInterval(() => {
            doSystemAnalyzeActiveUserTrend()
            doSystemAnalyzeTrafficUsage()
            doSystemAnalyzeUser()
        }, 15 * 1000);

        return () => {
            clearInterval(serverInfoInterval)
            clearInterval(systemAnalyzeInterval)
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
                                value: (getSizeNumber(serverInfo.jvmUsedMemory)),
                                description: <StatisticCard.Statistic
                                    title={getTitle(serverInfo.jvmTotalMemory)}
                                    value={getPercentage(serverInfo.jvmTotalMemory, serverInfo.jvmUsedMemory)}
                                    valueRender={(node) => {
                                        return <div
                                            className={getValueClassName(serverInfo.jvmTotalMemory, serverInfo.jvmUsedMemory)}>
                                            {node}
                                        </div>
                                    }}
                                />,
                            }}
                            chart={
                                <div id={WorkplaceJvmECharts} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard.Divider type={value.isMobile ? 'horizontal' : 'vertical'}/>
                        <StatisticCard
                            statistic={{
                                title: '系统内存使用',
                                value: (getSizeNumber(serverInfo.memoryUsed)),
                                description: <StatisticCard.Statistic
                                    title={getTitle(serverInfo.memoryTotal)}
                                    value={getPercentage(serverInfo.memoryTotal, serverInfo.memoryUsed)}
                                    valueRender={(node) => {
                                        return <div
                                            className={getValueClassName(serverInfo.memoryTotal, serverInfo.memoryUsed)}>
                                            {node}
                                        </div>
                                    }}/>,
                            }}
                            chart={
                                <div id={WorkplaceMemoryECharts} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard.Divider type={value.isMobile ? 'horizontal' : 'vertical'}/>
                        <StatisticCard
                            statistic={{
                                title: 'CPU使用',
                                value: (serverInfo.cpuUsed || 0),
                                description: <StatisticCard.Statistic
                                    title={`占比 ${CpuTotal}`}
                                    value={getPercentage(CpuTotal, serverInfo.cpuUsed)}
                                    valueRender={(node) => {
                                        return <div
                                            className={getValueClassName(CpuTotal, serverInfo.cpuUsed)}>
                                            {node}
                                        </div>
                                    }}
                                />,
                            }}
                            chart={
                                <div id={WorkplaceCpuECharts} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard.Divider type={value.isMobile ? 'horizontal' : 'vertical'}/>
                        <StatisticCard
                            statistic={{
                                title: '磁盘使用',
                                value: (getSizeNumber(serverInfo.diskUsed)),
                                description: <StatisticCard.Statistic
                                    title={getTitle(serverInfo.diskTotal)}
                                    value={getPercentage(serverInfo.diskTotal, serverInfo.diskUsed)}
                                    valueRender={(node) => {
                                        return <div
                                            className={getValueClassName(serverInfo.diskTotal, serverInfo.diskUsed)}>
                                            {node}
                                        </div>
                                    }}
                                />,
                            }}
                            chart={
                                <div id={WorkplaceDiskECharts} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                    </StatisticCard.Group>

                    <ProCard
                        title="平台概览"
                        extra={moment().format('YYYY年M月D日 dddd')}
                        split={value.isMobile ? 'horizontal' : 'vertical'}
                        headerBordered
                        bordered
                    >
                        <ProCard split="horizontal">
                            <ProCard split="horizontal">
                                <ProCard split="vertical">
                                    <StatisticCard
                                        statistic={{
                                            title: '昨日活跃人数',
                                            value: getNumber(activeUser.yesterdayTotal),
                                            description: <StatisticCard.Statistic
                                                title={`较每日活跃人数 ${getNumber(activeUser.dailyTotal)}`}
                                                value={getProportion(activeUser.dailyTotal, activeUser.yesterdayTotal)}
                                                trend={getTrend(activeUser.dailyTotal, activeUser.yesterdayTotal)}/>,
                                        }}
                                    />
                                    <StatisticCard
                                        statistic={{
                                            title: '总用户数',
                                            value: getNumber(analyzeUser.total),
                                            suffix: '个'
                                        }}
                                    />
                                </ProCard>
                                <ProCard split="vertical">
                                    <StatisticCard
                                        statistic={{
                                            title: '昨日新增用户',
                                            value: getNumber(analyzeUser.yesterdayAddTotal),
                                            description: <StatisticCard.Statistic
                                                title={`较每日新增用户 ${getNumber(analyzeUser.dailyAddTotal)}`}
                                                value={getProportion(analyzeUser.dailyAddTotal, analyzeUser.yesterdayAddTotal)}
                                                trend={getTrend(analyzeUser.dailyAddTotal, analyzeUser.yesterdayAddTotal)}/>,
                                        }}
                                    />
                                    <StatisticCard
                                        statistic={{
                                            title: '昨日注销用户',
                                            value: getNumber(analyzeUser.yesterdayDeleteTotal),
                                            description: <StatisticCard.Statistic
                                                title={`较每日注销用户 ${getNumber(analyzeUser.dailyDeleteTotal)}`}
                                                value={getProportion(analyzeUser.dailyDeleteTotal, analyzeUser.yesterdayDeleteTotal)}
                                                trend={getTrend(analyzeUser.dailyDeleteTotal, analyzeUser.yesterdayDeleteTotal)}/>,
                                        }}
                                    />
                                </ProCard>
                            </ProCard>
                            <StatisticCard
                                title="活跃人数走势"
                                chart={
                                    <div id={ActiveUserTrendECharts} className={"w-315 h-180"}/>
                                }
                            />
                        </ProCard>
                        <StatisticCard
                            title="流量占用情况"
                            chart={
                                <div id={TrafficUsageECharts} className={"w-315 h-315"}/>
                            }
                        />
                    </ProCard>
                </>
            }}
        </RouteContext.Consumer>
    )
}

// 获取：趋势
function getTrend(source: number = 0, target: number = 0) {
    return getProportion(source, target, false) > 0 ? 'up' : 'down'
}

// 获取：比例，source 被占多少比例
function getProportion(source: number = 0, target: number = 0, addStrFlag: boolean = true) {
    let res: string | number = 0;
    if (source !== 0) {
        res = Math.round((target - source) / source * 10000) / 100
    }
    if (addStrFlag) {
        res = res + '%'
    }
    return res
}

// 获取：默认为 0的数字
function getNumber(number: number = 0) {
    return number
}

// 通过 byte获取 mb的字符串
function getSizeNumber(number: number = 0) {
    if (number > 1024 * 1024 * 1024) {
        return Math.round((number / 1024 / 1024 / 1024) * 100) / 100 + 'G'
    }
    return Math.round((number / 1024 / 1024) * 100) / 100 + 'MB'
}

// 获取：占比
function getTitle(total: number = 0) {
    return '占比 ' + getSizeNumber(total)
}

// 获取：value的 className
function getValueClassName(total: number = 0, value: number = 0) {
    return getDangerFlag(total, value) ? 'red4' : 'green3'
}

// 获取：参数是否危险
function getDangerFlag(total: number = 0, value: number = 0) {
    return getPercentage(total, value, false) > 85
}

// 获取：百分比
function getPercentage(total: number = 0, value: number = 0, addStrFlag: boolean = true) {
    let res: string | number = 0;
    if (total !== 0) {
        res = Math.round((value / total) * 10000) / 100
    }
    if (addStrFlag) {
        res = res + '%'
    }
    return res
}
