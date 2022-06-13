// @ts-ignore
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

const fs = require('fs')
const axios = require('axios')

// 同步 openApi到 api文件夹
function start() {
    // @ts-ignore
    axios.get<IOpenApi>("http://localhost:9527/v3/api-docs").then(({data}: { data: IOpenApi }) => {
        data.tags.forEach(item => {

            // User Login Controller -> UserLoginController
            const controllerName = item.description.replace(new RegExp(' ', 'g'), '')

            // TODO：del
            if (controllerName !== 'MenuController') {
                return
            }

            // 写入文件
            fs.writeFile('./src/api/' + controllerName + '.ts', "export default {}", (err: any) => {
                if (err) {
                    throw err
                }
                console.log("操作成功 :>> " + controllerName + ".ts")
            })

        })
    })
}

start()
