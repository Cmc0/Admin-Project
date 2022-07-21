package com.admin.im.controller;

import com.admin.common.model.vo.ApiResultVO;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.dto.ImSessionPageDTO;
import com.admin.im.model.vo.ImSessionPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/im")
@Api(tags = "即时通讯-管理")
public class ImController {

    @Resource
    ImService baseService;

    @PostMapping(value = "/send")
    @ApiOperation(value = "发送消息")
    public ApiResultVO<String> send(@RequestBody @Valid ImSendDTO dto) {
        return ApiResultVO.ok(baseService.send(dto));
    }

    @PostMapping(value = "/sessionPage")
    @ApiOperation(value = "分页排序查询：即时通讯会话")
    public ApiResultVO<Page<ImSessionPageVO>> sessionPage(@RequestBody @Valid ImSessionPageDTO dto) {
        return ApiResultVO.ok(baseService.sessionPage(dto));
    }

}
