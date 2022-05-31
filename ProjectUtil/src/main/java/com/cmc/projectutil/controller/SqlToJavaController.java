package com.cmc.projectutil.controller;

import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.model.dto.NotBlankStrDTO;
import com.cmc.projectutil.model.vo.ApiResultVO;
import com.cmc.projectutil.service.SqlToJavaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sqlToJava")
@Api(tags = "sql转java")
public class SqlToJavaController {

    @Resource
    SqlToJavaService baseService;

    @ApiOperation(value = "sql转java")
    @PostMapping
    public ApiResultVO<String> sqlToJava(@RequestBody @Valid NotBlankStrDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.API_RESULT_OK.getMsg(), baseService.sqlToJava(dto));
    }

}
