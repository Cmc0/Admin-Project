export const TINYINT_ONE = "tinyint(1)"

interface IColumnTypeRefEnum {
    columnType: string,
    javaType: string,
    tsType: string,
}

const ColumnTypeRefEnum: Record<string, IColumnTypeRefEnum> = {
    Boolean: {
        columnType: TINYINT_ONE,
        javaType: 'Boolean',
        tsType: 'boolean',
    },
    tinyint: {
        columnType: "tinyint",
        javaType: 'Byte',
        tsType: 'number',
    },
    Integer: {
        columnType: "int",
        javaType: 'Integer',
        tsType: 'number',
    },
    bigint: {
        columnType: "bigint",
        javaType: 'Long',
        tsType: 'number',
    },
    varchar: {
        columnType: "varchar",
        javaType: 'String',
        tsType: 'string',
    },
    text: {
        columnType: "text",
        javaType: 'String',
        tsType: 'string',
    },
    longtext: {
        columnType: "longtext",
        javaType: 'String',
        tsType: 'string',
    },
    datetime: {
        columnType: "datetime",
        javaType: 'Date',
        tsType: 'Date',
    },
}

const defaultColumnTypeRefEnum: IColumnTypeRefEnum = {
    columnType: "varchar",
    javaType: 'String',
    tsType: 'string[]',
}

function getByColumnType(columnType: string) {

    if (columnType === TINYINT_ONE) {
        return ColumnTypeRefEnum.Boolean
    }

    let result = defaultColumnTypeRefEnum

    columnType = columnType.split('(')[0]

    Object.keys(ColumnTypeRefEnum).some(item => {
        const flag = ColumnTypeRefEnum[item].columnType === columnType
        if (flag) {
            result = ColumnTypeRefEnum[item]
        }
        return flag
    })

    return result

}

function getByJavaType(javaType: string) {

    let result = defaultColumnTypeRefEnum

    Object.keys(ColumnTypeRefEnum).some(item => {
        const flag = ColumnTypeRefEnum[item].javaType === javaType
        if (flag) {
            result = ColumnTypeRefEnum[item]
        }
        return flag
    })

    return result

}

export default {getByColumnType, getByJavaType}


