package com.admin.im.controller;

import com.admin.im.service.ImService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/im")
@Api(tags = "即时通讯-管理")
public class ImController {

    @Resource
    ImService baseService;

}
