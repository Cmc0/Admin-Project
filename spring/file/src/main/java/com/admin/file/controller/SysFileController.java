package com.admin.file.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.file.model.dto.SysFileDownloadDTO;
import com.admin.file.model.dto.SysFileRemoveDTO;
import com.admin.file.model.dto.SysFileUploadDTO;
import com.admin.file.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/sysFile")
@Api(tags = "文件-管理")
public class SysFileController {

    @Resource
    SysFileService baseService;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public ApiResultVO<String> upload(SysFileUploadDTO dto) {
        return ApiResultVO.ok("上传成功", baseService.upload(dto));
    }

    @ApiOperation(value = "公用，文件下载（不需要登录）")
    @GetMapping("/publicDownload")
    public void publicDownload(@RequestParam(value = "url") String url) {
        baseService.publicDownload(url);
    }

    @ApiOperation(value = "文件下载")
    @PostMapping("/download")
    @PreAuthorize("hasAuthority('sysFile:download')")
    public void download(@RequestBody @Valid SysFileDownloadDTO dto) {
        baseService.download(dto);
    }

    @ApiOperation(value = "文件批量删除")
    @PostMapping("/remove")
    public ApiResultVO<String> remove(@RequestBody @Valid SysFileRemoveDTO dto) {
        return ApiResultVO.ok(baseService.remove(dto));
    }

}
