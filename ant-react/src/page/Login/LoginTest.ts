import {FormInstance} from "antd";
import {UserLoginByPasswordDTO} from "@/api/UserLoginController";
import {execTest} from "../../../util/PageTestUtil";

export default function (useForm: FormInstance<UserLoginByPasswordDTO>) {
    console.log('LoginTest')

    execTest({
        name: '错误登录测试-1',
        func: () => {
            useForm.setFieldsValue({
                account: 'admin',
                password: 'suancai123'
            })

            useForm.submit()
        }
    })

    execTest({
        name: '错误登录测试-2',
        func: () => {
            useForm.setFieldsValue({
                account: 'admin123',
                password: 'suancai'
            })

            useForm.submit()
        }
    })


}
