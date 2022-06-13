// 通过 openApi，覆盖 /src/api 下的文件
import $http from "./HttpUtil";
import CommonConstant from "@/model/constant/CommonConstant";

interface IOpenApiPathResRequestBody {
    content: {
        "application/json": {
            schema: {
                $ref: string // 例如：#/components/schemas/AddOrderNoDTO
            }
        }
    }
}

interface IOpenApiPathRes {
    tags: IOpenApiTag[]
    summary: string // 接口描述
    operationId: string // 入参
    requestBody: IOpenApiPathResRequestBody
}

interface IOpenApiPath {
    post: IOpenApiPathRes
}

interface IOpenApiTag { // 例如：{ name": "用户-登录", description": "User Login Controller" }
    name: string
    description: string
}

interface IOpenApiComponentSchemaProperty {
    type: string // 字段类型：string integer boolean array
    description: string // 字段描述
    example?: boolean // 默认值，目前只有 boolean 有默认值
    items?: { type: string } // 如果是集合，才有此值
}

interface IOpenApi {
    tags: IOpenApiTag[]
    paths: IOpenApiPath[]
    components: {
        schemas: Record<string, Record<'properties', IOpenApiComponentSchemaProperty>>
    }
}

export default function () {
    $http.get<IOpenApi>(CommonConstant.OPEN_API_URL).then(({data}) => {
        data.tags.forEach(item => {
            const controllerName = item.description.replaceAll(' ', '');

        })
    })
}
