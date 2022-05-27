package com.cmc.projectutil.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * {@link com.cmc.projectutil.model.vo.CodeGeneratePageVO}
 */
@Data
public class CodeGenerateDTO {

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableComment;

    @ApiModelProperty(value = "表名（驼峰）")
    private String tableNameCamelCase;

    @ApiModelProperty(value = "表名（驼峰并首字母大写）")
    private String tableNameCamelCaseUpperFirst;

    @ApiModelProperty(value = "字段 list")
    private List<CodeGenerateListDTO> columnList;

    @ApiModelProperty(value = "父类类名（simpleName）只会为 null，或者实际的值，不会为空字符串")
    private String supperClassName;

    @ApiModelProperty(value = "没有父类字段 list，不会为 null，但是不一定有元素")
    private List<CodeGenerateListDTO> noSupperClassColumnList;

}
