import {RouteContext, RouteContextType, StatisticCard} from "@ant-design/pro-components";
import {Statistic} from "antd";

export default function () {
    return (
        <RouteContext.Consumer>
            {(value: RouteContextType) => {
                return <>
                    <StatisticCard.Group direction={value.isMobile ? 'column' : 'row'}>
                        <StatisticCard
                            statistic={{
                                title: '内存使用',
                                value: '108MB',
                                description: <Statistic title="占比" value="61.5%"/>,
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
                                title: '内存使用',
                                value: '108MB',
                                description: <Statistic title="占比" value="61.5%"/>,
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
                                title: '内存使用',
                                value: '108MB',
                                description: <Statistic title="占比" value="61.5%"/>,
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
                                title: '内存使用',
                                value: '108MB',
                                description: <Statistic title="占比" value="61.5%"/>,
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
