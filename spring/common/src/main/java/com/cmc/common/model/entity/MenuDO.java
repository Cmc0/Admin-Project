package com.cmc.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "menu")
@Data
@ApiModel(description = "菜单主表")
public class MenuDO extends BaseEntityFour<MenuDO> {

    @ApiModelProperty(value = "页面的 path，备注：path不能重复")
    private String path;

    @ApiModelProperty(value = "权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById")
    private String auths;

    @ApiModelProperty(value = "菜单名")
    private String name;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到")
    private Boolean showFlag;

    @ApiModelProperty(value = "是否外链，即，打开页面会在一个新的窗口打开")
    private Boolean linkFlag;

    @ApiModelProperty(value = "路由：linkFlag === false 时使用，不必填")
    private String router;

    @ApiModelProperty(value = "重定向：linkFlag === false 时使用，不必填")
    private String redirect;

    @ApiModelProperty(value = "是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单")
    private Boolean firstFlag;

    @ApiModelProperty(value = "是否是权限菜单，权限菜单：不显示，只代表菜单权限")
    private Boolean authFlag;

}
