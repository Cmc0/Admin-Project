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

const typeList = ['string', 'integer', 'boolean', 'array'] as const

type IOpenApiComponentSchemaPropertyFormat = 'byte' | 'int64' | 'date-time' | 'int32'
type IOpenApiComponentSchemaPropertyType = typeof typeList[number]

interface IOpenApiComponentSchemaProperty { // 例如：{ AddOrderNoDTO: { title: '', description: '', ... }}
    $ref?: string // $ref 有值时，type 和 format没值
    type?: IOpenApiComponentSchemaPropertyType // 字段类型
    format?: IOpenApiComponentSchemaPropertyFormat
    description: string // 字段描述
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

// 获取：model/entity下面的 ts文件名
let entityNameList: string[] = fs.readdirSync("./src/model/entity")
entityNameList = entityNameList.map(item => item.split('.ts')[0])

function getInterfaceType(type: string, format: IOpenApiComponentSchemaPropertyFormat, componentProperty: IOpenApiComponentSchemaProperty, fileData: string) {

    if (type === 'integer') {
        type = 'number'
    } else if ((type === 'string' && format === 'byte')) {
        type = 'boolean'
    } else if (type === 'array') {
        if (componentProperty.items) {
            if (componentProperty.items.type) {
                type = getInterfaceType(componentProperty.items.type, componentProperty.items.format!, componentProperty, fileData) + '[]'
            } else if (componentProperty.items.$ref) {
                const refSplitList = componentProperty.items.$ref.split('/');
                type = refSplitList[refSplitList.length - 1];
            }
        }
    }

    return type
}

function getComponentNameByFullName(componentFullName: string) {
    const splitList = componentFullName.split('/')
    return splitList[splitList.length - 1]
}

function getFileDataFromModelDir(componentName: string, fileData: string) {

    let value = ''

    if (dtoNameList.includes(componentName)) {

        value = `import ${componentName} from "@/model/dto/${componentName}";\n`

    } else if (entityNameList.includes(componentName)) {

        value = `import ${componentName} from "@/model/entity/${componentName}";\n`
    }

    if (value && !fileData.includes(value)) {
        fileData = value + fileData
    }

    return fileData;
}

// 写：interface
function writeInterface(componentName: string, fileData: string, component: Record<string, IOpenApiComponentSchemaProperty>) {

    if (dtoNameList.includes(componentName) || entityNameList.includes(componentName)) {
        fileData = getFileDataFromModelDir(componentName, fileData)
        return fileData
    }

    let value = `export interface ${componentName}`

    const orderFlag = Object.keys(component).some(item => item === 'order'); // 是否包含 order字段

    if (orderFlag) {
        fileData = getFileDataFromModelDir('MyPageDTO', fileData)
        value += ' extends MyPageDTO'
    }
    value += ' {\n'

    if (fileData.includes(value)) {
        return fileData
    }

    fileData += value

    Object.keys(component).forEach(item => {

        let type = 'error'
        if (component[item].type) {

            let format = component[item].format!
            type = getInterfaceType(component[item].type!, format, component[item], fileData)
            fileData = getFileDataFromModelDir(type, fileData)

        } else if (component[item].$ref) {

            const refSplitList = component[item].$ref!.split('/');
            type = refSplitList[refSplitList.length - 1];
            fileData = getFileDataFromModelDir(type, fileData);
        }

        fileData += `    ${item}?: ${type} // ${component[item].description}\n`
    })

    fileData += "}\n\n"

    return fileData
}

// 获取 正则匹配的字符串
function getMatchStr(responsesName: string, regexp: string | RegExp = /«(.*)»/) {

    const matchList = responsesName.match(regexp);

    if (matchList && matchList.length) {
        responsesName = matchList[1]
    }

    return responsesName;
}

// 同步 openApi到 api文件夹
function start() {

    // @ts-ignore
    axios.get<IOpenApi>("http://localhost:9527/v3/api-docs").then(({data}: { data: IOpenApi }) => {

        const tagNameAndPathResMap: Record<string, IOpenApiPathRes[]> = {}

        Object.keys(data.paths).forEach(item => {
            if (!data.paths[item].post) {
                return
            }
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
            componentMap[item] = data.components.schemas[item].properties
        })

        data.tags.forEach(item => {

            // User Login Controller -> UserLoginController
            const controllerName = item.description.replace(new RegExp(' ', 'g'), '')

            let fileData = 'import $http from "../../util/HttpUtil";\n\n'

            const pathList = tagNameAndPathResMap[item.name]

            if (!pathList) {
                return;
            }

            pathList.forEach((subItem, subIndex) => {

                let requestBodyFlag = true // 是否有入参
                let responsesFlag = true // 是否有返回值
                let requestBodyName = '' // 入参 bean的名称
                let responsesName = '' // 返回值 bean的名称
                let pageFlag = false // 是否是 page请求
                let treeFlag = false // 是否是 tree请求
                let infoByIdFlag = false // 是否是 infoById请求

                const apiName = toHump(subItem.uri.slice(1), /\/(\w)/g)

                if (subItem.requestBody) {
                    const requestBodyFullName = subItem.requestBody.content["application/json"].schema.$ref
                    requestBodyName = getComponentNameByFullName(requestBodyFullName)
                    if (requestBodyName === 'NotNullId' && apiName.includes("InfoById")) {
                        infoByIdFlag = true
                    }
                    const requestBody = componentMap[requestBodyName]
                    requestBodyFlag = Boolean(requestBody)
                    if (requestBodyFlag) {
                        fileData = writeInterface(requestBodyName, fileData, requestBody)
                    }
                }

                if (subItem.responses) {
                    if (!subItem.responses["200"].content) {
                        return
                    }
                    const responsesFullName = subItem.responses["200"].content["*/*"].schema.$ref;
                    responsesName = getComponentNameByFullName(responsesFullName)
                    responsesName = getMatchStr(responsesName);
                    if (responsesName.startsWith("Page«")) {
                        responsesName = getMatchStr(responsesName);
                        pageFlag = true
                    } else if (responsesName.startsWith("List«")) {
                        responsesName = getMatchStr(responsesName);
                        treeFlag = true
                    }
                    const responses = componentMap[responsesName]
                    // @ts-ignore
                    responsesFlag = responses && !typeList.includes(responsesName)
                    if (responsesFlag) {
                        fileData = writeInterface(responsesName, fileData, responses)
                    }
                }

                fileData += `// ${item.name} ${subItem.summary}\n`

                fileData += `export function ${apiName}(`

                if (requestBodyName) {
                    fileData += 'form: ' + requestBodyName
                }

                fileData += `) {\n`

                if (pageFlag) {
                    fileData += `    return $http.myProPagePost`
                } else if (treeFlag) {
                    fileData += `    return $http.myProTreePost`
                } else if (infoByIdFlag) {
                    fileData += `    return $http.myProPost`
                } else {
                    fileData += `    return $http.myPost`
                }

                if (responsesName) {
                    fileData += `<${responsesName}>`
                }

                fileData += `('${subItem.uri}'`

                if (requestBodyName) {
                    fileData += ', form'
                }

                if (subIndex === pathList.length - 1) {
                    fileData += ')\n}\n'
                } else {
                    fileData += ')\n}\n\n'
                }
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

console.log('执行开始 ↓ ' + new Date().toLocaleString())

start()

console.log('执行成功 ↑ ' + new Date().toLocaleString())
