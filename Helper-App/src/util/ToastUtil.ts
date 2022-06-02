import {message} from "antd";

export function ToastInfo(msg: string, duration: number = 4) {
    message.info(msg, duration);
}

export function ToastSuccess(msg: string, duration: number = 4) {
    message.success(msg, duration);
}

export function ToastError(msg: string, duration: number = 4) {
    message.error(msg, duration);
}
