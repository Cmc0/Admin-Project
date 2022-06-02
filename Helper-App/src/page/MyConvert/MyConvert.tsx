import {Button, Drawer, Dropdown, Form, Input, Menu, PageHeader, Space, Typography} from "antd";
import {useState} from "react";

interface IFunctionButton {
    name: string // 按钮名称
    functionStr: string // 按钮执行的方法
}

export default function () {

    const [fbList, setFbList] = useState<IFunctionButton[]>([]); // 方法按钮集合
    const [source, setSource] = useState<string>(''); // 要转换的内容
    const [result, setResult] = useState<string>(''); // 转换后的内容
    const [drawerTitle, setDrawerTitle] = useState<string>(''); // drawer的 title
    const [drawerVisible, setDrawerVisible] = useState<boolean>(false); // drawer的 visible
    const [drawerForm, setDrawerForm] = useState<IFunctionButton>({name: '测试', functionStr: 'setResult(source)'}); // drawer的 form
    const [drawerUseForm] = Form.useForm(); // drawer的 useForm

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
                        fbList?.map((item, index) => (
                                <Dropdown.Button
                                    key={index}
                                    overlay={<Menu
                                        onClick={(e) => {
                                            console.log(e.key)
                                            if (e.key === 'delFunction') {
                                                fbList.splice(index, 1)
                                                setFbList(fbList)
                                            }
                                        }}
                                        items={[
                                            {
                                                key: 'delFunction',
                                                label: '删除方法',
                                            },
                                        ]}
                                    />}
                                    onClick={() => {
                                        new Function("source", "setResult", item.functionStr)(source, setResult)
                                    }}
                                >{item.name}</Dropdown.Button>
                            )
                        )
                    }
                </>
                <Button onClick={() => {
                    setDrawerTitle('添加方法，可以直接使用【source】【setResult()】')
                    setDrawerVisible(true)
                }}>添加方法</Button>
            </Space>

            <Drawer
                closable={false}
                onClose={() => {
                    setDrawerVisible(false)
                }}
                title={drawerTitle}
                visible={drawerVisible} size={"large"}
                footer={<Space>
                    <Button
                        type={"primary"}
                        onClick={() => {
                            drawerUseForm.submit()
                        }}
                    >确定</Button>
                    <Button
                        onClick={() => {
                            drawerUseForm.resetFields()
                        }}>重置</Button>
                </Space>}>
                <Form
                    layout={"vertical"}
                    form={drawerUseForm}
                    initialValues={drawerForm}
                    onFinish={(form: IFunctionButton) => {
                        fbList.push(form)
                        setFbList(fbList)
                    }}>
                    <Form.Item
                        label="方法名"
                        name="name"
                        rules={[{required: true}]}
                    >
                        <Input allowClear/>
                    </Form.Item>
                    <Form.Item
                        label="方法"
                        name="functionStr"
                        rules={[{required: true}]}
                    >
                        <Input.TextArea allowClear rows={28}/>
                    </Form.Item>
                </Form>
            </Drawer>

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
