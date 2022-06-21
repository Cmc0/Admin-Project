package com.admin.dict.model.vo;

import com.admin.common.model.entity.SysDictDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictTreeVO extends SysDictDO {

    @ApiModelProperty(value = "字典子节点")
    private List<SysDictTreeVO> children;
}
