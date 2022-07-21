package com.admin.im.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImContentPageDTO extends MyPageDTO {

    @NotBlank
    @ApiModelProperty(value = "冗余字段，toType_toId")
    private String sId;

}
