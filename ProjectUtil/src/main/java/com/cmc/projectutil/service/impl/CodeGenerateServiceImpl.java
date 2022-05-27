package com.cmc.projectutil.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.cmc.projectutil.util.CodeGenerateHelperUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public String forSpring(List<CodeGenerateForSpringListDTO> list) {

        String rootFileName = System.getProperty("user.dir") + "/src/main/java/generate/";

        File rootFile = FileUtil.file(rootFileName);
        FileUtil.mkdir(rootFile); // 不存在则会创建，存在了则不进行操作

        FileUtil.mkdir(rootFileName + "/controller");
        FileUtil.mkdir(rootFileName + "/model");
        FileUtil.mkdir(rootFileName + "/model/dto");
        FileUtil.mkdir(rootFileName + "/model/vo");
        FileUtil.mkdir(rootFileName + "/model/entity");
        FileUtil.mkdir(rootFileName + "/service");
        FileUtil.mkdir(rootFileName + "/service/impl");
        FileUtil.mkdir(rootFileName + "/mapper");

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

                // 寻找：对应的 java类型
                String columnJavaType;
                if (CodeGenerateHelperUtil.TINYINT_ONE.equals(subItem.getColumnType())) {
                    columnJavaType = CodeGenerateHelperUtil.COLUMN_TYPE_REF_JAVA_MAP.get(subItem.getColumnType());
                } else {
                    String subBefore = StrUtil.subBefore(subItem.getColumnType(), "(", false);
                    columnJavaType = CodeGenerateHelperUtil.COLUMN_TYPE_REF_JAVA_MAP.get(subBefore);
                }
                subItem.setColumnJavaType(columnJavaType);
            }

            String supperClassName = CodeGenerateHelperUtil.getSupperClassName(item.getValue());

            CodeGenerateForSpringDTO codeGenerateForSpringDTO = new CodeGenerateForSpringDTO();
            codeGenerateForSpringDTO.setTableName(tableName);
            codeGenerateForSpringDTO.setTableComment(tableComment);
            codeGenerateForSpringDTO.setTableNameCamelCase(tableNameCamelCase);
            codeGenerateForSpringDTO.setTableNameCamelCaseUpperFirst(tableNameCamelCaseUpperFirst);
            codeGenerateForSpringDTO.setColumnList(item.getValue());
            codeGenerateForSpringDTO.setSupperClassName(supperClassName);

            JSONObject json = JSONUtil.parseObj(codeGenerateForSpringDTO);

            generateSpringController(rootFileName, engine, codeGenerateForSpringDTO, json);

            generateSpringModel(rootFileName, engine, codeGenerateForSpringDTO, json);

            generateSpringServiceAndMapper(rootFileName, engine, codeGenerateForSpringDTO, json);

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 生成 spring-service
     */
    @SneakyThrows
    private void generateSpringServiceAndMapper(String rootFileName, TemplateEngine engine,
        CodeGenerateForSpringDTO codeGenerateForSpringDTO, JSONObject json) {

        Template template = engine.getTemplate("BaseService.java.ftl");

        File file = FileUtil.file(
            rootFileName + "/service/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst() + "Service.java");
        FileUtil.touch(file);

        template.render(json, file);

        template = engine.getTemplate("BaseServiceImpl.java.ftl");

        file = FileUtil.file(
            rootFileName + "/service/impl/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst()
                + "ServiceImpl.java");
        FileUtil.touch(file);

        template.render(json, file);

        template = engine.getTemplate("BaseMapper.java.ftl");

        file = FileUtil.file(
            rootFileName + "/mapper/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst() + "Mapper.java");
        FileUtil.touch(file);

        template.render(json, file);

        template = engine.getTemplate("BaseMapper.xml.ftl");

        file = FileUtil.file(
            rootFileName + "/mapper/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst() + "Mapper.xml");
        FileUtil.touch(file);

        template.render(json, file);

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
        FileUtil.touch(file);

        if (codeGenerateForSpringDTO.getSupperClassName() != null) {

            Set<String> supperClassFieldNameSet =
                CodeGenerateHelperUtil.BASE_ENTITY_MAP.get(codeGenerateForSpringDTO.getSupperClassName());

            CodeGenerateForSpringDTO newDTO =
                BeanUtil.copyProperties(codeGenerateForSpringDTO, CodeGenerateForSpringDTO.class);

            // 不要父类有的属性
            List<CodeGenerateForSpringListDTO> newColumnList = newDTO.getColumnList().stream()
                .filter(it -> !supperClassFieldNameSet.contains(it.getColumnNameCamelCase()))
                .collect(Collectors.toList());

            newDTO.setColumnList(newColumnList);

            template.render(JSONUtil.parseObj(newDTO), file);
        } else {
            template.render(json, file);
        }
        // DO ↑

        template = engine.getTemplate("BaseInfoByIdVO.java.ftl");

        file = FileUtil.file(rootFileName + "/model/vo/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst()
            + "InfoByIdVO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // InfoByIdVO ↑

        template = engine.getTemplate("BaseInsertOrUpdateDTO.java.ftl");

        file = FileUtil.file(rootFileName + "/model/dto/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst()
            + "InsertOrUpdateDTO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // InsertOrUpdateDTO ↑

        template = engine.getTemplate("BasePageDTO.java.ftl");

        file = FileUtil.file(
            rootFileName + "/model/dto/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst() + "PageDTO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // PageDTO ↑

        template = engine.getTemplate("BasePageVO.java.ftl");

        file = FileUtil.file(
            rootFileName + "/model/vo/" + codeGenerateForSpringDTO.getTableNameCamelCaseUpperFirst() + "PageVO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // PageVO ↑

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
        FileUtil.touch(file);

        template.render(json, file);
    }

    /**
     * 生成前端代码
     */
    @Override
    public String forAnt(List<CodeGenerateForSpringListDTO> list) {

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

}
