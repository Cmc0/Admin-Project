import {USER_CENTER_KEY_TWO} from "./Center";
import {List} from "antd";

interface IUserCenterSetting {
    title: string
    description?: string
    actionName: string
    actionFun: () => void
}

export default function () {
    return (
        <List<IUserCenterSetting>
            header={USER_CENTER_KEY_TWO}
            rowKey="title"
            dataSource={[
                {
                    title: '密码',
                    actionName: "修改密码",
                    actionFun: () => {

                    }
                },
                {
                    title: '邮箱',
                    description: '1*********@qq.com',
                    actionName: "修改邮箱",
                    actionFun: () => {

                    }
                },
                {
                    title: '刷新令牌',
                    description: '刷新之后，执行任意操作，都会要求重新登录，用于：不修改密码，退出所有登录',
                    actionName: "执行刷新",
                    actionFun: () => {

                    }
                },
                {
                    title: '登录记录',
                    actionName: "查看记录",
                    actionFun: () => {

                    }
                },
                {
                    title: '账号注销',
                    actionName: "立即注销",
                    actionFun: () => {

                    }
                },
            ]}
            renderItem={item => (
                <List.Item actions={[
                    <a className={"fw-600"} key="1" onClick={() => {
                        if (item.actionFun) {
                            item.actionFun()
                        }
                    }}>
                        {item.actionName}
                    </a>
                ]}>
                    <List.Item.Meta
                        title={item.title}
                        description={item.description}
                    />
                </List.Item>
            )}
        />
    )
}
