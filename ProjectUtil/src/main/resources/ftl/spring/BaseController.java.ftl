package generate.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.model.dto.AddOrderNoDTO;
import com.cmc.projectutil.model.dto.NotEmptyIdSet;
import com.cmc.projectutil.model.dto.NotNullId;
import com.cmc.projectutil.model.vo.ApiResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import generate.service.${tableNameCamelCaseUpperFirst}Service;
import generate.model.dto.${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO;
import generate.model.dto.${tableNameCamelCaseUpperFirst}PageDTO;
import generate.model.vo.${tableNameCamelCaseUpperFirst}PageVO;
import generate.model.vo.${tableNameCamelCaseUpperFirst}InfoByIdVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/${tableNameCamelCase}")
@Api(tags = "${tableComment}")
public class ${tableNameCamelCaseUpperFirst}Controller {

    @Resource
    ${tableNameCamelCaseUpperFirst}Service baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('${tableNameCamelCase}:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(
        @RequestBody @Valid ${tableNameCamelCaseUpperFirst}InsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('${tableNameCamelCase}:page')")
    public ApiResultVO<Page<${tableNameCamelCaseUpperFirst}PageVO>> myPage(
        @RequestBody @Valid ${tableNameCamelCaseUpperFirst}PageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('${tableNameCamelCase}:page')")
    public ApiResultVO<List<${tableNameCamelCaseUpperFirst}PageVO>> tree(
        @RequestBody @Valid ${tableNameCamelCaseUpperFirst}PageDTO dto) {
        return ApiResultVO.ok(baseService.tree(dto));
    }

    @ApiOperation(value = "删除（可批量）")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('${tableNameCamelCase}:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('${tableNameCamelCase}:infoById')")
    public ApiResultVO<${tableNameCamelCaseUpperFirst}InfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('${tableNameCamelCase}:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid AddOrderNoDTO dto) {
        return ApiResultVO.ok(baseService.addOrderNo(dto));
    }

}
