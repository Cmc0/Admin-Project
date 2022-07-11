package com.admin.bulletin.controller;

import com.admin.bulletin.model.dto.SysBulletinInsertOrUpdateDTO;
import com.admin.bulletin.model.dto.SysBulletinPageDTO;
import com.admin.bulletin.model.dto.SysBulletinUserSelfPageDTO;
import com.admin.bulletin.model.entity.SysBulletinDO;
import com.admin.bulletin.model.vo.SysBulletinPageVO;
import com.admin.bulletin.model.vo.SysBulletinUserSelfPageVO;
import com.admin.bulletin.service.SysBulletinService;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.vo.ApiResultVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/sysBulletin")
@Api(tags = "公告-管理")
public class SysBulletinController {

    @Resource
    SysBulletinService baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysBulletin:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysBulletinInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "发布 公告")
    @PostMapping("/publish")
    @PreAuthorize("hasAuthority('sysBulletin:insertOrUpdate')")
    public ApiResultVO<String> publish(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.publish(notNullId));
    }

    @ApiOperation(value = "撤回 公告")
    @PostMapping("/revoke")
    @PreAuthorize("hasAuthority('sysBulletin:insertOrUpdate')")
    public ApiResultVO<String> revoke(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.revoke(notNullId));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysBulletin:page')")
    public ApiResultVO<Page<SysBulletinPageVO>> myPage(@RequestBody @Valid SysBulletinPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysBulletin:infoById')")
    public ApiResultVO<SysBulletinDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysBulletin:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @PostMapping("/userSelfPage")
    @ApiOperation(value = "分页排序查询：当前用户可以查看的公告")
    public ApiResultVO<Page<SysBulletinUserSelfPageVO>> userSelfPage(
        @RequestBody @Valid SysBulletinUserSelfPageDTO dto) {
        return ApiResultVO.ok(baseService.userSelfPage(dto));
    }

    @PostMapping("/userSelfCount")
    @ApiOperation(value = "获取：当前用户可以查看的公告，总数")
    public ApiResultVO<Long> userSelfCount() {
        return ApiResultVO.ok(baseService.userSelfCount());
    }

}
