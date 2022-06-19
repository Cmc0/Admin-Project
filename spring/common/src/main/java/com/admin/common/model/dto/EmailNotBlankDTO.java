package com.admin.common.model.dto;

import com.admin.common.model.constant.BaseRegexConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class EmailNotBlankDTO {

    @Size(max = 200)
    @NotBlank
    @Pattern(regexp = BaseRegexConstant.EMAIL)
    @ApiModelProperty(value = "邮箱")
    private String email;

}
