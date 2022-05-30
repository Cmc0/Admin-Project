import {message} from "antd";

export function ToastSuccess(msg: string, duration: number = 4) {
    message.success(msg, duration);
}

export function ToastError(msg: string, duration: number = 4) {
    message.error(msg, duration);
}
