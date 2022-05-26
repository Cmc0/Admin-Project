package com.cmc.projectutil.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.mapper.CodeGenerateMapper;
import com.cmc.projectutil.model.dto.CodeGenerateForSpringDTO;
import com.cmc.projectutil.model.dto.CodeGenerateForSpringListDTO;
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
    public String codeGenerateForSpring(List<CodeGenerateForSpringListDTO> list) {

        String rootFileName = System.getProperty("user.dir") + "/src/main/java/generate/" ;

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

        Map<String, List<CodeGenerateForSpringListDTO>> groupMap =
            list.stream().collect(Collectors.groupingBy(CodeGeneratePageVO::getTableName));

        for (Map.Entry<String, List<CodeGenerateForSpringListDTO>> item : groupMap.entrySet()) {

            String tableName = item.getKey();
            String tableComment = item.getValue().get(0).getTableComment();

            String tableNameCamelCase = StrUtil.toCamelCase(tableName);
            String tableNameCamelCaseUpperFirst = StrUtil.upperFirst(tableNameCamelCase);

            for (CodeGenerateForSpringListDTO subItem : item.getValue()) {
                subItem.setColumnNameCamelCase(StrUtil.toCamelCase(subItem.getColumnName()));
            }

            CodeGenerateForSpringDTO codeGenerateForSpringDTO = new CodeGenerateForSpringDTO();
            codeGenerateForSpringDTO.setTableName(tableName);
            codeGenerateForSpringDTO.setTableComment(tableComment);
            codeGenerateForSpringDTO.setTableNameCamelCase(tableNameCamelCase);
            codeGenerateForSpringDTO.setTableNameCamelCaseUpperFirst(tableNameCamelCaseUpperFirst);
            codeGenerateForSpringDTO.setColumnList(item.getValue());

            JSONObject json = JSONUtil.parseObj(codeGenerateForSpringDTO);

            generateSpringController(rootFileName, engine, codeGenerateForSpringDTO, json);

            generateSpringModel(rootFileName, engine, codeGenerateForSpringDTO, json);

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 生成 spring-model
     */
    @SneakyThrows
    private void generateSpringModel(String rootFileName, TemplateEngine engine,
        CodeGenerateForSpringDTO codeGenerateForSpringDTO, JSONObject json) {

        Template template = engine.getTemplate("BaseDO.java.ftl");

        File file = FileUtil.file(
            rootFileName + "/model/entity/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst() + "DO.java");
        file.createNewFile();

        template.render(json, file);

    }

    /**
     * 生成 spring-controller
     */
    @SneakyThrows
    private void generateSpringController(String rootFileName, TemplateEngine engine,
        CodeGenerateForSpringDTO codeGenerateForSpringDTO, JSONObject json) {

        Template template = engine.getTemplate("BaseController.java.ftl");

        File file = FileUtil.file(
            rootFileName + "/controller/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst()
                + "Controller.java");
        file.createNewFile();

        template.render(json, file);
    }
}
