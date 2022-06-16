package com.admin.menu.model.dto;

import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * {@link com.admin.common.model.entity.BaseMenuDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MenuInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotBlank
    @ApiModelProperty(value = "菜单名")
    private String name;

    @ApiModelProperty(value = "页面的 path，备注：path不能重复")
    private String path;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "父节点id（顶级则为0）")
    private Long parentId;

    @ApiModelProperty(value = "权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById")
    private String auths;

    @ApiModelProperty(value = "是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到")
    private boolean showFlag;

    @ApiModelProperty(value = "排序号（值越大越前面，默认为 0）")
    private int orderNo;

    @ApiModelProperty(value = "启用/禁用")
    private boolean enableFlag;

    @ApiModelProperty(value = "是否外链，即，打开页面会在一个新的窗口打开，可以配合 router")
    private boolean linkFlag;

    @ApiModelProperty(value = "路由")
    private String router;

    @ApiModelProperty(value = "重定向：linkFlag === false 时使用，不必填，暂时未使用")
    private String redirect;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单")
    private boolean firstFlag;

    @ApiModelProperty(value = "角色 idSet")
    private Set<Long> roleIdSet;

    @ApiModelProperty(value = "是否是权限菜单，权限菜单：不显示，只代表菜单权限")
    private boolean authFlag;

}
