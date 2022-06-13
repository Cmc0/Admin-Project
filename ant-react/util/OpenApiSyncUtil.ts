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

interface IOpenApiPathResResponses {
    200: {
        content: {
            "*/*": {
                schema: {
                    $ref: string // 例如：#/components/schemas/ApiResultVO«string»
                }
            }
        }
    }
}

interface IOpenApiPathRes {
    tags: string[] // 分组/所属文件
    summary: string // 接口描述
    requestBody?: IOpenApiPathResRequestBody // 入参
    responses: IOpenApiPathResResponses // 返回值
}

interface IOpenApiPath {
    post: IOpenApiPathRes
}

interface IOpenApiTag { // 例如：{ name: "用户-登录", description: "User Login Controller" }
    name: string
    description: string
}

interface IOpenApiComponentSchemaProperty { // 例如：{ AddOrderNoDTO: { title: '', description: '', ... }}
    type: string // 字段类型：string integer boolean array
    description: string // 字段描述
    example?: boolean // 默认值，目前只有 boolean 有默认值
    items?: { type: string } // 如果是集合，才有此值
}

interface IOpenApi {
    tags: IOpenApiTag[]
    paths: Record<string, IOpenApiPath>
    components: {
        schemas: Record<string, Record<'properties', Record<string, IOpenApiComponentSchemaProperty>>>
    }
}

const fs = require('fs')
const axios = require('axios')

// 同步 openApi到 api文件夹
function start() {
    // @ts-ignore
    axios.get<IOpenApi>("http://localhost:9527/v3/api-docs").then(({data}: { data: IOpenApi }) => {

        const tagNameAndPathResMap: Record<string, IOpenApiPathRes[]> = {}

        Object.keys(data.paths).forEach(item => {
            const tagName = data.paths[item].post.tags[0]
            if (tagNameAndPathResMap[tagName]) {
                tagNameAndPathResMap[tagName] = [...tagNameAndPathResMap[tagName], data.paths[item].post]
            } else {
                tagNameAndPathResMap[tagName] = [data.paths[item].post]
            }
        })

        const componentMap: Record<string, Record<string, IOpenApiComponentSchemaProperty>> = {}

        Object.keys(data.components.schemas).forEach(item => {
            componentMap["#/components/schemas/" + item] = data.components.schemas[item].properties
        })

        data.tags.forEach(item => {

            // User Login Controller -> UserLoginController
            const controllerName = item.description.replace(new RegExp(' ', 'g'), '')

            // TODO：del
            if (controllerName !== 'MenuController') {
                return
            }

            let fileData = 'import $http from "../../util/HttpUtil";\n\n'

            const pathList = tagNameAndPathResMap[item.name]

            if (!pathList) {
                return;
            }

            pathList.forEach(subItem => {
                if (!subItem.requestBody) {
                    // TODO：如果没有入参
                    return
                }
                const requestBodyName = subItem.requestBody.content["application/json"].schema.$ref
                const requestBody = componentMap[requestBodyName];
                if (!requestBody) {
                    return
                }
                // 如果有入参
                const splitList = requestBodyName.split('/');
                const dtoName = splitList[splitList.length - 1];

                fileData += `// ${item.name} ${subItem.summary || '暂无接口描述'}\n`
                fileData += `export interface ${dtoName} {\n`

                Object.keys(requestBody).forEach(deepNode => {
                    let type = requestBody[deepNode].type
                    if (type === 'integer') {
                        type = 'number'
                    } else if (type === 'array') {
                        type = requestBody[deepNode].items!.type
                        if (type === 'integer') {
                            type = 'number'
                        }
                        type += '[]'
                    }
                    fileData += `    ${deepNode}?: ${type} // ${requestBody[deepNode].description}\n`
                })

                fileData += "}\n\n"
            })

            // 写入文件
            fs.writeFile('./src/api/' + controllerName + '.ts', fileData, (err: any) => {
                if (err) {
                    throw err
                }
                console.log("操作成功 :>> " + controllerName + ".ts")
            })

        })
    })
}

start()
