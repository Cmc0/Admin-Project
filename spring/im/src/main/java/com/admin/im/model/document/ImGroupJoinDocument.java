package com.admin.im.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImGroupJoinDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

    @ApiModelProperty(value = "加入的群组归属用户 id")
    private Long createId;

    @ApiModelProperty(value = "加入群组的时间")
    private Date createTime;

    @ApiModelProperty(value = "群组主表 id，uuid")
    private String gId;

    @ApiModelProperty(value = "备注名称")
    private String remark;

    @ApiModelProperty(value = "退出群组的时间")
    private Date outTime;

}
