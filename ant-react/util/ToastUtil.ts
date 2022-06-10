import {message} from "antd";

export function ToastSuccess(msg: string, duration: number = 4) {
    message.success(msg, duration);
}

export function ToastInfo(msg: string, duration: number = 4) {
    message.info(msg, duration);
}

export function ToastWarning(msg: string, duration: number = 4) {
    message.warning(msg, duration);
}

export function ToastError(msg: string, duration: number = 4) {
    message.error(msg, duration);
}
