package generate.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmc.projectutil.model.entity.BaseEntityFour;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "${tableName}")
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}DO extends BaseEntityFour<${tableNameCamelCaseUpperFirst}DO> {

<#list columnList as column>
    <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
    </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

</#list>
}
