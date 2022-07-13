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
import CommonConstant from "@/model/constant/CommonConstant";
import EChartOption = echarts.EChartOption;

const WorkplaceJvmEChartsId = "WorkplaceJvmEChartsId"
const WorkplaceMemoryEChartsId = "WorkplaceMemoryEChartsId"
const WorkplaceCpuEChartsId = "WorkplaceCpuEChartsId"
const WorkplaceDiskEChartsId = "WorkplaceDiskEChartsId"

const ActiveUserTrendEChartsId = "ActiveUserTrendEChartsId"
const TrafficUsageEChartsId = "TrafficUsageEChartsId"

const CpuTotal = 100

function setServerInfoEChartsOption(eachChartsRef: React.MutableRefObject<echarts.EChartsType | undefined>, freeTotal: number | undefined, usedTotal: number | undefined, total: number | undefined) {

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
                ],
            }
        ]
    } as EChartOption<EChartOption.SeriesPie>)

}

function setActiveUserTrendEChartsOption(data: SystemAnalyzeActiveUserTrendVO[] | undefined, activeUserTrendEChartsRef: React.MutableRefObject<echarts.EChartsType | undefined>) {

    activeUserTrendEChartsRef.current?.hideLoading()

    const monthDataStrList = data?.map(it => it.monthDataStr!);
    const totalList = data?.map(it => it.total);

    activeUserTrendEChartsRef.current?.setOption({
        xAxis: {
            data: monthDataStrList
        },
        tooltip: {
            trigger: 'axis'
        },
        yAxis: {},
        series: [
            {
                data: totalList,
                type: 'line',
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        {
                            offset: 0,
                            color: '#5B8FF9'
                        },
                        {
                            offset: 1,
                            color: '#FFFFFF'
                        }
                    ])
                },
                markLine: {
                    data: [{type: 'average'}]
                }
            }
        ]
    } as EChartOption<EChartOption.SeriesLine>)

}

function setTrafficUsageEChartsOption(data: SystemAnalyzeTrafficUsageVO[] | undefined, trafficUsageEChartsRef: React.MutableRefObject<echarts.EChartsType | undefined>, isMobile: React.MutableRefObject<boolean>) {

    trafficUsageEChartsRef.current?.hideLoading()

    const dataList = data?.map(it => ({name: it.categoryStr, value: it.total}));

    trafficUsageEChartsRef.current?.setOption({
        tooltip: {
            trigger: 'item'
        },
        legend: isMobile.current ?
            {} : {
                orient: 'vertical',
                right: '10',
                top: '10',
                bottom: '10',
            },
        series: [
            {
                type: 'pie',
                radius: ['43%', '70%'],
                data: dataList
            }
        ]
    } as EChartOption<EChartOption.SeriesPie>)

}

export default function () {

    const [serverInfo, setServerInfo] = useState<ServerWorkInfoVO>({});
    const [activeUser, setActiveUser] = useState<SystemAnalyzeActiveUserVO>({});
    const [activeUserTrendList, setActiveUserTrendList] = useState<SystemAnalyzeActiveUserTrendVO[] | undefined>([]);
    const [trafficUsage, setTrafficUsage] = useState<SystemAnalyzeTrafficUsageVO[] | undefined>([]);
    const [analyzeUser, setAnalyzeUser] = useState<SystemAnalyzeUserVO>({});

    const workplaceJvmEChartsRef = useRef<echarts.EChartsType>()
    const workplaceMemoryEChartsRef = useRef<echarts.EChartsType>()
    const workplaceCpuEChartsRef = useRef<echarts.EChartsType>()
    const workplaceDiskEChartsRef = useRef<echarts.EChartsType>()

    const activeUserTrendEChartsRef = useRef<echarts.EChartsType>()
    const trafficUsageEChartsRef = useRef<echarts.EChartsType>()

    const activeUserTrendEChartsAdapterRef = useRef<HTMLDivElement>(null)
    const trafficUsageEChartsAdapterRef = useRef<HTMLDivElement>(null)

    const isMobile = useRef<boolean>(false)

    function doSetServerInfo() {
        serverWorkInfo().then(res => {
            setServerInfoEChartsOption(workplaceJvmEChartsRef, res.data.jvmFreeMemory, res.data.jvmUsedMemory, res.data.jvmTotalMemory)
            setServerInfoEChartsOption(workplaceMemoryEChartsRef, res.data.memoryAvailable, res.data.memoryUsed, res.data.memoryTotal)
            setServerInfoEChartsOption(workplaceCpuEChartsRef, (CpuTotal - res.data.cpuUsed!), res.data.cpuUsed, CpuTotal)
            setServerInfoEChartsOption(workplaceDiskEChartsRef, res.data.diskUsable, res.data.diskUsed, res.data.diskTotal)
            setServerInfo(res.data)
        })
    }

    function doSystemAnalyzeActiveUser() {
        systemAnalyzeActiveUser().then(res => {
            setActiveUser(res.data)
        })
    }

    function doSystemAnalyzeActiveUserTrend() {
        systemAnalyzeActiveUserTrend().then(res => {
            setActiveUserTrendEChartsOption(res.data, activeUserTrendEChartsRef)
            setActiveUserTrendList(res.data)
        })
    }

    function doSystemAnalyzeTrafficUsage() {
        systemAnalyzeTrafficUsage().then(res => {
            setTrafficUsageEChartsOption(res.data, trafficUsageEChartsRef, isMobile)
            setTrafficUsage(res.data)
        })
    }

    function doSystemAnalyzeUser() {
        systemAnalyzeUser().then(res => {
            setAnalyzeUser(res.data)
        })
    }

    useEffect(() => {
        // 服务器运行情况 ↓
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
        workplaceJvmEChartsRef.current = echarts.init(document.getElementById(WorkplaceJvmEChartsId)!)
        workplaceMemoryEChartsRef.current = echarts.init(document.getElementById(WorkplaceMemoryEChartsId)!)
        workplaceCpuEChartsRef.current = echarts.init(document.getElementById(WorkplaceCpuEChartsId)!)
        workplaceDiskEChartsRef.current = echarts.init(document.getElementById(WorkplaceDiskEChartsId)!)

        workplaceJvmEChartsRef.current?.showLoading()
        workplaceMemoryEChartsRef.current?.showLoading()
        workplaceCpuEChartsRef.current?.showLoading()
        workplaceDiskEChartsRef.current?.showLoading()
        doSetServerInfo()
        const serverInfoInterval = setInterval(doSetServerInfo, 15 * 1000);
        // 服务器运行情况 ↑

        // 平台概览 ↓
        if (activeUserTrendEChartsRef.current) {
            activeUserTrendEChartsRef.current.dispose()
        }
        if (trafficUsageEChartsRef.current) {
            trafficUsageEChartsRef.current.dispose()
        }

        const activeUserTrendEChartsElement = document.getElementById(ActiveUserTrendEChartsId)!;
        const trafficUsageEChartsElement = document.getElementById(TrafficUsageEChartsId)!;

        isMobile.current = window.innerWidth < CommonConstant.MOBILE_WIDTH

        const activeUserTrendEChartsAdapterWidth = activeUserTrendEChartsAdapterRef.current?.clientWidth!;

        activeUserTrendEChartsElement.style.width = activeUserTrendEChartsAdapterWidth + 'px'
        activeUserTrendEChartsElement.style.height = activeUserTrendEChartsAdapterWidth / (isMobile.current ? 1.1 : 2) + 'px'

        const trafficUsageEChartsAdapterWidth = trafficUsageEChartsAdapterRef.current?.clientWidth! / (isMobile.current ? 1 : 1.5);

        trafficUsageEChartsElement.style.width = trafficUsageEChartsAdapterWidth + 'px'
        trafficUsageEChartsElement.style.height = trafficUsageEChartsAdapterWidth + (isMobile.current ? 120 : 0) + 'px'

        activeUserTrendEChartsRef.current = echarts.init(activeUserTrendEChartsElement)
        trafficUsageEChartsRef.current = echarts.init(trafficUsageEChartsElement)

        activeUserTrendEChartsRef.current?.showLoading()
        trafficUsageEChartsRef.current?.showLoading()

        doSystemAnalyzeActiveUser()
        doSystemAnalyzeActiveUserTrend()
        doSystemAnalyzeTrafficUsage()
        doSystemAnalyzeUser()

        const systemAnalyzeInterval = setInterval(() => {
            doSystemAnalyzeActiveUserTrend()
            doSystemAnalyzeTrafficUsage()
            doSystemAnalyzeUser()
        }, 15 * 1000);

        // 平台概览 ↑

        return () => {
            clearInterval(serverInfoInterval)
            clearInterval(systemAnalyzeInterval)
        }
    }, [])

    return (
        <RouteContext.Consumer>
            {(routeContextType: RouteContextType) => {
                return <>
                    <StatisticCard.Group direction={routeContextType.isMobile ? 'column' : 'row'}>
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
                                <div id={WorkplaceJvmEChartsId} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard.Divider type={routeContextType.isMobile ? 'horizontal' : 'vertical'}/>
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
                                <div id={WorkplaceMemoryEChartsId} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard.Divider type={routeContextType.isMobile ? 'horizontal' : 'vertical'}/>
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
                                <div id={WorkplaceCpuEChartsId} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                        <StatisticCard.Divider type={routeContextType.isMobile ? 'horizontal' : 'vertical'}/>
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
                                <div id={WorkplaceDiskEChartsId} className={"w-100 h-100"}/>
                            }
                            chartPlacement="left"
                        />
                    </StatisticCard.Group>

                    <ProCard
                        title="平台概览"
                        extra={moment().format('YYYY年M月D日 dddd')}
                        split={routeContextType.isMobile ? 'horizontal' : 'vertical'}
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
                                    <div ref={activeUserTrendEChartsAdapterRef}>
                                        <div id={ActiveUserTrendEChartsId}/>
                                    </div>
                                }
                            />
                        </ProCard>
                        <StatisticCard
                            title="流量占用情况"
                            chart={
                                <div ref={trafficUsageEChartsAdapterRef}>
                                    <div id={TrafficUsageEChartsId}/>
                                </div>
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
