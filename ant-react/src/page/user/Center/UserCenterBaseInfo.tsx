import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {SysUserBaseInfoVO} from "@/api/SysUserController";
import {ProDescriptions, ProFormUploadButton} from "@ant-design/pro-components";
import {EyeOutlined} from "@ant-design/icons/lib";
import CommonConstant from "@/model/constant/CommonConstant";
import {sysFileUpload} from "@/api/SysFileController";
import {ToastSuccess} from "../../../../util/ToastUtil";

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
                    renderFormItem: () => <ProFormUploadButton
                        // 备注：如果选择的不是 accept里面的文件类型，则不会进行任何操作
                        accept=".png,.jpeg,.jpg"
                        fieldProps={{
                            customRequest: (options) => {
                                const formData = new FormData()
                                formData.append('file', options.file)
                                formData.append('bucketName', 'public')
                                formData.append('folderName', 'avatar')
                                sysFileUpload(formData, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => {
                                    ToastSuccess(res.msg)
                                })
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
