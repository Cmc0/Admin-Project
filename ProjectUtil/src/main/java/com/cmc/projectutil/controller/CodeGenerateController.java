package com.cmc.projectutil.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.model.dto.CodeGenerateForSpringListDTO;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.ApiResultVO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import com.cmc.projectutil.service.CodeGenerateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/codeGenerate")
@Api(tags = "代码生成控制器")
public class CodeGenerateController {

    @Resource
    CodeGenerateService baseService;

    @ApiOperation(value = "分页排序查询")
    @PostMapping(value = "/page")
    public ApiResultVO<Page<CodeGeneratePageVO>> myPage(@RequestBody @Valid CodeGeneratePageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "生成后台代码")
    @PostMapping(value = "/forSpring")
    public ApiResultVO<String> forSpring(@RequestBody @Valid List<CodeGenerateForSpringListDTO> list) {
        return ApiResultVO.ok(baseService.forSpring(list));
    }

    @ApiOperation(value = "生成前端代码")
    @PostMapping(value = "/forAnt")
    public ApiResultVO<String> forAnt(@RequestBody @Valid List<CodeGenerateForSpringListDTO> list) {
        return ApiResultVO.ok(baseService.forAnt(list));
    }

}
