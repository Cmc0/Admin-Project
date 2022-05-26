package generate.model.dto;

import com.cmc.projectutil.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * {@link generate.model.entity.${tableNameCamelCaseUpperFirst}DO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}PageDTO extends MyPageDTO{

<#list columnList as column>
    <#if column.columnComment?? && column.columnComment != "">
    @ApiModelProperty(value = "${column.columnComment}")
    </#if>
    private ${column.columnJavaType} ${column.columnNameCamelCase};

</#list>
}
