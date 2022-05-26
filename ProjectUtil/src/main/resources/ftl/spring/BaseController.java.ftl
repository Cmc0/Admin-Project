package generate.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.model.dto.AddOrderNoDTO;
import com.cmc.projectutil.model.dto.NotEmptyIdSet;
import com.cmc.projectutil.model.dto.NotNullId;
import com.cmc.projectutil.model.vo.ApiResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import generate.model.dto.${fileName}InsertOrUpdateDTO;
import generate.model.dto.${fileName}PageDTO;
import generate.model.vo.${fileName}PageVO;
import generate.model.vo.${fileName}InfoByIdVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/${name}")
@Api(tags = "${fileTags}")
public class ${fileName}Controller {

    @Resource
    ${fileName}Service baseService;

    @ApiOperation(value = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('${name}:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid ${fileName}InsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @ApiOperation(value = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('${name}:page')")
    public ApiResultVO<Page<${fileName}PageVO>> myPage(@RequestBody @Valid ${fileName}PageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @ApiOperation(value = "查询：树结构")
    @PostMapping("/tree")
    @PreAuthorize("hasAuthority('${name}:page')")
    public ApiResultVO<List<${fileName}PageVO>> tree(@RequestBody @Valid ${fileName}PageDTO dto) {
        return ApiResultVO.ok(baseService.tree(dto));
    }

    @ApiOperation(value = "删除（可批量）")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('${name}:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @ApiOperation(value = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('${name}:infoById')")
    public ApiResultVO<${fileName}InfoByIdVO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @ApiOperation(value = "通过主键 idSet，加减排序号")
    @PostMapping("/addOrderNo")
    @PreAuthorize("hasAuthority('${name}:insertOrUpdate')")
    public ApiResultVO<String> addOrderNo(@RequestBody @Valid AddOrderNoDTO dto) {
        return ApiResultVO.ok(baseService.addOrderNo(dto));
    }

}
