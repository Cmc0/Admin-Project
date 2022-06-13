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

type IOpenApiComponentSchemaPropertyFormat = 'byte' | 'int64' | 'date-time' | 'int32'
type IOpenApiComponentSchemaPropertyType = 'string' | 'integer' | 'boolean' | 'array'

interface IOpenApiComponentSchemaProperty { // 例如：{ AddOrderNoDTO: { title: '', description: '', ... }}
    $ref?: string // $ref 有值时，type 和 format没值
    type?: IOpenApiComponentSchemaPropertyType // 字段类型
    format?: IOpenApiComponentSchemaPropertyFormat
    description: string // 字段描述
    example?: boolean // 默认值，目前只有 boolean 有默认值
    items?: {
        $ref?: string // $ref 有值时，type 和 format没值
        type?: string
        format?: IOpenApiComponentSchemaPropertyFormat
    } // 如果是集合，才有此值
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

function getInterfaceType(type: string, format: IOpenApiComponentSchemaPropertyFormat, componentProperty: IOpenApiComponentSchemaProperty) {

    if (type === 'integer') {
        type = 'number'
    } else if (type === 'string' && format === 'byte') {
        type = 'boolean'
    } else if (type === 'array') {
        if (componentProperty.items && componentProperty.items.type) {
            type = getInterfaceType(componentProperty.items.type, componentProperty.items.format!, componentProperty) + '[]'
        }
    }

    return type
}

// 写：interface
function interfaceWrite(componentFullName: string, fileData: string, component: Record<string, IOpenApiComponentSchemaProperty>) {

    const splitList = componentFullName.split('/');
    const componentName = splitList[splitList.length - 1];

    fileData += `export interface ${componentName} {\n`

    Object.keys(component).forEach(item => {

        let type = 'error'
        if (component[item].type) {
            let format = component[item].format!
            type = getInterfaceType(component[item].type!, format, component[item])
        } else if (component[item].$ref) {
            const refSplitList = component[item].$ref!.split('/');
            const refComponentName = refSplitList[refSplitList.length - 1];
            type = refComponentName
        }

        fileData += `    ${item}?: ${type} // ${component[item].description}\n`
    })

    fileData += "}\n\n"

    return fileData
}

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

                if (subItem.responses) {
                    const responsesName = subItem.responses["200"].content["*/*"].schema.$ref;

                }

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
                fileData = interfaceWrite(requestBodyName, fileData, requestBody);
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
