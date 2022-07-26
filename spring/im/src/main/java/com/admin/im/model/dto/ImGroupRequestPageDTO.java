package com.admin.im.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImGroupRequestPageDTO extends MyPageDTO {

    @Size(min = 32, max = 32)
    @ApiModelProperty(value = "群组主表 id，uuid，备注：传了这个则表示是群组管理员级别，在查看入群申请")
    private String gId;

}
