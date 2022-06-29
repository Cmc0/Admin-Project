import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {SysUserBaseInfoVO} from "@/api/SysUserController";
import {ProDescriptions, ProFormUploadButton} from "@ant-design/pro-components";
import {EyeOutlined} from "@ant-design/icons/lib";
import CommonConstant from "@/model/constant/CommonConstant";
import {sysFileUpload} from "@/api/SysFileController";
import {ToastError, ToastSuccess} from "../../../../util/ToastUtil";
import {CheckImageFileSize, CheckImageFileType} from "../../../../util/FileUtil";

export default function () {

    // const [readyOnly, setReadyOnly] = useState<boolean>(true);

    return (
        // <ProForm<SysUserBaseInfoVO>
        //     autoFocusFirstInput
        //     request={() => {
        //         return Promise.resolve(JSON.parse(localStorage.getItem(LocalStorageKey.USER_BASE_INFO) || '{}'));
        //     }}
        //     onFinish={async (form) => {
        //         console.log(form)
        //         return true
        //     }}>
        //     <ProFormText readonly={readyOnly} name="nickname" label="昵称"/>
        // </ProForm>

        <ProDescriptions<SysUserBaseInfoVO>
            title="个人资料"
            request={() => {
                return Promise.resolve({
                    success: true,
                    data: JSON.parse(localStorage.getItem(LocalStorageKey.USER_BASE_INFO) || '{}'),
                });
            }}
            editable={{
                onSave: async (key, record, originRow) => {
                    console.log(key, record, originRow);
                    return true;
                },
            }}
            column={1}
            columns={[
                {
                    title: '头像',
                    dataIndex: 'avatarUrl',
                    valueType: 'image',
                    fieldProps: {
                        preview: {
                            mask: <EyeOutlined title={"预览"}/>
                        }
                    },
                    renderText: (text) => {
                        return text ? text : CommonConstant.RANDOM_AVATAR_URL
                    },
                    renderFormItem: () =>
                        <ProFormUploadButton
                            accept={CommonConstant.IMAGE_FILE_ACCEPT_TYPE}
                            fieldProps={{
                                customRequest: (options) => {
                                    const formData = new FormData()
                                    formData.append('file', options.file)
                                    formData.append('bucketName', 'public')
                                    formData.append('folderName', 'avatar')
                                    sysFileUpload(formData, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => {
                                        ToastSuccess(res.msg)
                                    })
                                },
                                beforeUpload: (file, FileList) => {
                                    console.log(file)
                                    if (!CheckImageFileType(file.type)) {
                                        ToastError("暂不支持此文件类型：" + file.type + "，请重新选择")
                                        return false
                                    }
                                    if (!CheckImageFileSize(file.size, 2097152)) {
                                        ToastError("图片大于 2MB，请重新选择")
                                        return false
                                    }
                                    return true
                                }
                            }}
                            fileList={[]}
                        />
                },
                {
                    title: '昵称',
                    dataIndex: 'nickname'
                },
            ]}
        />
    )
}
