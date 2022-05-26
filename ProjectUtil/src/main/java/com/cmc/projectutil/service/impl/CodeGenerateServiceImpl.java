package com.cmc.projectutil.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.mapper.CodeGenerateMapper;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import com.cmc.projectutil.service.CodeGenerateService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodeGenerateServiceImpl implements CodeGenerateService {

    @Resource
    CodeGenerateMapper baseMapper;

    /**
     * 分页排序查询
     */
    @Override
    public Page<CodeGeneratePageVO> myPage(CodeGeneratePageDTO dto) {
        return baseMapper.myPage(dto.getPage(), dto);
    }

    /**
     * 生成后台代码
     */
    @SneakyThrows
    @Override
    public String codeGenerateForSpring(List<CodeGeneratePageVO> list) {

        Map<String, List<CodeGeneratePageVO>> groupMap =
            list.stream().collect(Collectors.groupingBy(CodeGeneratePageVO::getTableName));

        for (Map.Entry<String, List<CodeGeneratePageVO>> item : groupMap.entrySet()) {

        }

        TemplateEngine engine =
            TemplateUtil.createEngine(new TemplateConfig("ftl/spring", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("BaseController.java.ftl");

        Dict dict = Dict.create().set("name", "area").set("fileName", "Area").set("fileTags", "区域控制器");

        String rootFileName = System.getProperty("user.dir");
        rootFileName = rootFileName + "/src/main/java/generate/" ;

        File rootFile = FileUtil.file(rootFileName);
        rootFile.mkdirs(); // 不存在则会创建，存在了则不管

        File file = FileUtil.file(rootFileName + "/controller/AreaController.java");
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        file.createNewFile();

        template.render(dict, file);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}
