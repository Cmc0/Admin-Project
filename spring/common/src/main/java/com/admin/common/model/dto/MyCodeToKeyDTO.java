package com.admin.common.model.dto;

import com.admin.common.model.constant.BaseRegexConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MyCodeToKeyDTO {

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @ApiModelProperty(value = "验证码")
    private String code;

}
