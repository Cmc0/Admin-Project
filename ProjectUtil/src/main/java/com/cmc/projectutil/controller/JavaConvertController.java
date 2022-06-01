package com.cmc.projectutil.controller;

import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.model.dto.NotBlankStrDTO;
import com.cmc.projectutil.model.vo.ApiResultVO;
import com.cmc.projectutil.service.JavaConvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/convert")
@Api(tags = "java转换")
public class JavaConvertController {

    @Resource
    JavaConvertService baseService;

    @ApiOperation(value = "sql转java")
    @PostMapping(value = "/sqlToJava")
    public ApiResultVO<String> sqlToJava(@RequestBody @Valid NotBlankStrDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.API_RESULT_OK.getMsg(), baseService.sqlToJava(dto));
    }

    @ApiOperation(value = "java转ts")
    @PostMapping(value = "/javaToTs")
    public ApiResultVO<String> javaToTs(@RequestBody @Valid NotBlankStrDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.API_RESULT_OK.getMsg(), baseService.javaToTs(dto));
    }

}
