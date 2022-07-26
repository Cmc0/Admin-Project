package com.admin.im.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * {@link com.admin.im.model.document.ImGroupRequestDocument}
 */
@Data
public class ImGroupRequestDTO {

    @ApiModelProperty(value = "申请内容")
    private String content;

    @Size(min = 32, max = 32)
    @NotBlank
    @ApiModelProperty(value = "群组主表 id，uuid")
    private String gid;

}
