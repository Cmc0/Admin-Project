import {FormInstance} from "antd";
import {UserLoginByPasswordDTO} from "@/api/UserLoginController";
import {execTest} from "../../../util/PageTestUtil";

export default function (useForm: FormInstance<UserLoginByPasswordDTO>) {
    console.log('LoginTest')

    execTest({
        name: '全部未输入',
        func: () => {
            useForm.setFieldsValue({
                account: '',
                password: ''
            })

            useForm.submit()
        }
    })

    execTest({
        name: '账号未输入',
        func: () => {
            useForm.setFieldsValue({
                account: '',
                password: 'suancai123'
            })

            useForm.submit()
        }
    })

    execTest({
        name: '密码未输入',
        func: () => {
            useForm.setFieldsValue({
                account: 'admin',
                password: ''
            })

            useForm.submit()
        }
    })

    execTest({
        name: '密码错误',
        func: () => {
            useForm.setFieldsValue({
                account: 'admin',
                password: 'suancai123'
            })

            useForm.submit()
        }
    })

    execTest({
        name: '账号错误',
        func: () => {
            useForm.setFieldsValue({
                account: 'admin123',
                password: 'suancai'
            })

            useForm.submit()
        }
    })

    execTest({
        name: '登录成功',
        func: () => {
            useForm.setFieldsValue({
                account: 'admin',
                password: 'suancai'
            })

            useForm.submit()
        }
    })

}
