import {Button, Drawer, Dropdown, Form, Input, List, Menu, PageHeader, Popover, Space, Typography} from "antd";
import {ReactNode, useState} from "react";
import {ToastError, ToastSuccess} from "@/util/ToastUtil";
import {randomString} from "@/util/RandomUtil";
import {QuestionCircleOutlined} from "@ant-design/icons/lib";
import ExplainList, {ExplainTitList} from "@/page/MyConvert/ExplainList";
import StrUtil from "@/util/StrUtil";

interface IFunctionButton {
    id?: number // 按钮的 id，现在是 index（下标）
    name: string // 按钮名称
    functionStr: string // 按钮执行的方法
    loading?: boolean // 是否执行中
}

const defaultDrawerForm: IFunctionButton = {name: '', functionStr: ''}

export default function () {

    const [fbList, setFbList] = useState<IFunctionButton[]>([]); // 方法按钮集合
    const [source, setSource] = useState<string>(''); // 要转换的内容
    const [result, setResult] = useState<string>(''); // 转换后的内容
    const [drawerTitle, setDrawerTitle] = useState<ReactNode>(''); // drawer的 title
    const [drawerVisible, setDrawerVisible] = useState<boolean>(false); // drawer的 visible
    const [drawerInitForm] = useState<IFunctionButton>(defaultDrawerForm); // drawer的 initForm
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
                    <Typography.Text keyboard>要转换的内容（source）</Typography.Text>
                    <Button size={"small"} onClick={() => setSource('')}>清空</Button>
                </Space>
                <Input.TextArea className={"flex1 m-t-10"} value={source} onChange={(e) => {
                    setSource(e.target.value)
                }}/>
            </div>

            <Space align={"center"} direction={"vertical"} className={"m-l-r-20"}>
                <>
                    {
                        fbList?.map((item, index) => (
                                <Dropdown.Button
                                    loading={item.loading}
                                    size={"small"}
                                    key={index}
                                    overlay={<Menu
                                        onClick={(e) => {
                                            if (e.key === 'delFunction') {
                                                fbList.splice(index, 1)
                                                setFbList(fbList.concat())
                                            } else if (e.key === 'editFunction') {
                                                drawerUseForm.setFieldsValue({...item, id: index})
                                                setDrawerVisible(true)
                                            }
                                        }}
                                        items={[
                                            {
                                                key: 'editFunction',
                                                label: `编辑【${item.name}】`,
                                            },
                                            {
                                                danger: true,
                                                key: 'delFunction',
                                                label: `删除【${item.name}】`,
                                            },
                                        ]}
                                    />}
                                    onClick={() => {
                                        try {
                                            setResult('')
                                            item.loading = true
                                            setFbList(fbList.concat())
                                            new Function(...ExplainTitList, item.functionStr)(source, setResult, new StrUtil())
                                            ToastSuccess("操作成功 (*^▽^*)")
                                        } catch (e) {
                                            console.error(e)
                                            ToastError('操作失败 o(╥﹏╥)o')
                                        } finally {
                                            setTimeout(() => {
                                                item.loading = false
                                                setFbList(fbList.concat())
                                            }, 300)
                                        }
                                    }}
                                >{item.name}</Dropdown.Button>
                            )
                        )
                    }
                </>
                <Button
                    type={"primary"}
                    onClick={() => {
                        drawerUseForm.setFieldsValue({...defaultDrawerForm, name: randomString()})
                        setDrawerTitle(
                            <Space>
                                <span>添加方法</span>
                                <Popover content={
                                    <List
                                        size={"small"}
                                        dataSource={ExplainList}
                                        renderItem={item => <List.Item><List.Item.Meta
                                            title={item.title}
                                            description={item.description}
                                        /></List.Item>}
                                    />
                                } title="说明">
                                    <QuestionCircleOutlined/>
                                </Popover>
                            </Space>)
                        setDrawerVisible(true)
                    }}>添加方法</Button>
            </Space>

            <Drawer
                closable={false}
                onClose={() => {
                    drawerUseForm.resetFields()
                    setDrawerVisible(false)
                }}
                title={drawerTitle}
                visible={drawerVisible} size={"large"}
                footer={
                    <Space>
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
                    initialValues={drawerInitForm}
                    onFinish={(form: IFunctionButton) => {
                        if (form.id !== undefined) {
                            fbList[form.id] = {...form}
                            setFbList(fbList.concat())
                        } else {
                            fbList.push(form)
                            setFbList(fbList)
                        }
                        drawerUseForm.resetFields()
                        setDrawerVisible(false)
                    }}>
                    <Form.Item
                        hidden
                        name="id"
                    ><Input/></Form.Item>
                    <Form.Item
                        hidden
                        name="loading"
                    ><Input/></Form.Item>
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
                    <Typography.Text keyboard>转换后的内容（result）</Typography.Text>
                    <Button size={"small"} onClick={() => setResult('')}>清空</Button>
                </Space>
                <Input.TextArea className={"flex1 m-t-10"} value={result} onChange={(e) => {
                    setResult(e.target.value)
                }}/>
            </div>

        </div>

    </div>
}
