import {Button, Dropdown, Input, Menu, PageHeader, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import {ToastError, ToastInfo} from "@/util/ToastUtil";

interface IFunctionButton {
    name: string // 按钮名称
    functionStr: string // 按钮执行的方法
}

// 自定义：控制台的输出
function useEffectConsoleLog() {
    return useEffect(() => {

        const oldLog = console.log
        const oldError = console.error
        console.log = (msg) => {
            ToastInfo(msg)
        }
        console.error = (msg) => {
            ToastError(msg)
        }

        return () => {
            console.log = oldLog
            console.error = oldError
        }
    }, [])
}

export default function () {

    const [fbList, setFbList] = useState<IFunctionButton[]>([])

    const [source, setSource] = useState<string>('');
    const [result, setResult] = useState<string>('');

    useEffectConsoleLog()

    return <div className={"bc vwh100 flex-c"}>

        <PageHeader
            ghost={false}
            title="代码转换 Code Convert Helper"
            subTitle="致力于帮助开发者减少一些繁琐的开发语言转换"
        />

        <div className={"flex-center p-20 flex1"}>

            <div className={"flex-c wh100"}>
                <Space>
                    <Typography.Text keyboard>要转换的内容</Typography.Text>
                    <Button size={"small"}>清空</Button>
                </Space>
                <Input.TextArea className={"flex1 m-t-10"} value={source} onChange={(e) => {
                    setSource(e.target.value)
                }}/>
            </div>

            <Space direction={"vertical"} className={"m-l-r-20"}>
                <>
                    {
                        fbList?.map(item =>
                            <Button size={"large"} type={"primary"} onClick={() => {
                                new Function("source", item.functionStr)(source)
                            }}>{item.name}</Button>
                        )
                    }
                </>
                <Dropdown.Button overlay={<Menu
                    items={[
                        {
                            key: 'delFunction',
                            label: '删除方法',
                        },
                    ]}
                />}>添加方法</Dropdown.Button>
            </Space>

            <div className={"flex-c wh100"}>
                <Space>
                    <Typography.Text keyboard>转换后的内容</Typography.Text>
                    <Button size={"small"}>清空</Button>
                </Space>
                <Input.TextArea className={"flex1 m-t-10"} value={result} onChange={(e) => {
                    setResult(e.target.value)
                }}/>
            </div>

        </div>

    </div>
}
