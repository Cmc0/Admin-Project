package com.admin.im.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImGroupRequestPageDTO extends MyPageDTO {

    //    @Size(min = 32, max = 32)
    //    @ApiModelProperty(value = "群组主表 id，uuid")
    //    private String gId;

}
