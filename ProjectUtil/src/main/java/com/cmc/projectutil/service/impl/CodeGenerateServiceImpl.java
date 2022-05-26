package com.cmc.projectutil.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
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

        String rootFileName = System.getProperty("user.dir");
        rootFileName = rootFileName + "/src/main/java/generate/" ;

        File rootFile = FileUtil.file(rootFileName);
        rootFile.mkdirs(); // 不存在则会创建，存在了则不进行操作

        FileUtil.file(rootFileName + "/controller").mkdirs();
        FileUtil.file(rootFileName + "/model").mkdirs();
        FileUtil.file(rootFileName + "/model/dto").mkdirs();
        FileUtil.file(rootFileName + "/model/vo").mkdirs();
        FileUtil.file(rootFileName + "/model/entity").mkdirs();
        FileUtil.file(rootFileName + "/service").mkdirs();
        FileUtil.file(rootFileName + "/service/impl").mkdirs();

        TemplateEngine engine =
            TemplateUtil.createEngine(new TemplateConfig("ftl/spring", TemplateConfig.ResourceMode.CLASSPATH));

        Map<String, List<CodeGeneratePageVO>> groupMap =
            list.stream().collect(Collectors.groupingBy(CodeGeneratePageVO::getTableName));

        for (Map.Entry<String, List<CodeGeneratePageVO>> item : groupMap.entrySet()) {
            String name = item.getKey();
            String fileTags = item.getValue().get(0).getTableComment();

            String fileName = StrUtil.toCamelCase(name);
            fileName = StrUtil.upperFirst(fileName);

            generateSpringController(rootFileName, engine, name, fileTags, fileName);

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    @SneakyThrows
    private void generateSpringController(String rootFileName, TemplateEngine engine, String name, String fileTags,
        String fileName) {

        Template template = engine.getTemplate("BaseController.java.ftl");

        Dict dict = Dict.create().set("name", name).set("fileName", fileName).set("fileTags", fileTags);

        File file = FileUtil.file(rootFileName + "/controller/" + fileName + "Controller.java");
        file.createNewFile();

        template.render(dict, file);
    }
}
