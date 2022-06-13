// @ts-ignore
const fs = require('fs')
const axios = require('axios')

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
    uri: string // 例如：/menu/addOrderNo
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

// 获取：model/dto下面的 ts文件名
let dtoNameList: string[] = fs.readdirSync("./src/model/dto")
dtoNameList = dtoNameList.map(item => item.split('.ts')[0])

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
function writeInterface(componentFullName: string, fileData: string, component: Record<string, IOpenApiComponentSchemaProperty>) {

    const splitList = componentFullName.split('/');
    const componentName = splitList[splitList.length - 1];

    if (dtoNameList.includes(componentName)) {
        fileData = `import ${componentName} from "@/model/dto/${componentName}";\n` + fileData
        return fileData
    }

    fileData += `export interface ${componentName} {\n`

    Object.keys(component).forEach(item => {

        let type = 'error'
        if (component[item].type) {
            let format = component[item].format!
            type = getInterfaceType(component[item].type!, format, component[item])
        } else if (component[item].$ref) {
            const refSplitList = component[item].$ref!.split('/');
            type = refSplitList[refSplitList.length - 1];
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
            data.paths[item].post.uri = item // 这里赋值：uri
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

                let responsesFlag = true // 是否有返回值
                let requestBodyFlag = true // 是否有入参

                if (subItem.requestBody) {
                    const requestBodyFullName = subItem.requestBody.content["application/json"].schema.$ref
                    const requestBody = componentMap[requestBodyFullName]
                    requestBodyFlag = Boolean(requestBody)
                    if (requestBodyFlag) {
                        fileData = writeInterface(requestBodyFullName, fileData, requestBody)
                    }
                }

                if (subItem.responses) {
                    const responsesFullName = subItem.responses["200"].content["*/*"].schema.$ref;
                    const responses = componentMap[responsesFullName]
                    responsesFlag = responses && !responsesFullName.includes('ApiResultVO«string»')
                    if (responsesFlag) {
                        fileData = writeInterface(responsesFullName, fileData, responses)
                    }
                }

                fileData += `// ${item.name} ${subItem.summary}\n`

                const apiName = toHump(subItem.uri.slice(1), /\/(\w)/g)

                fileData += `export function ${apiName}() {\n}\n\n`

            })

            // 写入文件
            fs.writeFileSync('./src/api/' + controllerName + '.ts', fileData)
        })
    })
}

// 正则表达式 转换驼峰
function toHump(name: string, searchValue: string | RegExp = /\_(\w)/g) {
    return name.replace(searchValue, (all, letter) => {
        return letter.toUpperCase()
    })
}

start()
