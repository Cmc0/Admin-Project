import {FormInstance} from "antd";
import {execTest} from "../../../util/PageTestUtil";
import {UserRegByEmailDTO} from "@/api/UserRegController";

export default function (useForm: FormInstance<UserRegByEmailDTO>) {
    console.log('RegisterTest')

    execTest({
        name: '全部未输入',
        func: () => {
            useForm.setFieldsValue({
                code: '',
                email: '',
                origPassword: '',
                password: '',
            })

            useForm.submit()
        }
    })

}
