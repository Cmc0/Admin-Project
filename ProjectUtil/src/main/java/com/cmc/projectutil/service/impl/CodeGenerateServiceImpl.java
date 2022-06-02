package com.cmc.projectutil.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.model.dto.CodeGenerateDTO;
import com.cmc.projectutil.model.dto.CodeGenerateItemDTO;
import com.cmc.projectutil.model.enums.ColumnTypeRefEnum;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import com.cmc.projectutil.service.CodeGenerateService;
import com.cmc.projectutil.util.CodeGenerateHelperUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodeGenerateServiceImpl implements CodeGenerateService {

    /**
     * 生成后台代码
     */
    @SneakyThrows
    @Override
    public String forSpring(List<CodeGenerateItemDTO> list) {

        String rootFileName = System.getProperty("user.dir") + "/src/main/java/generate/spring";

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

        Map<String, List<CodeGenerateItemDTO>> groupMap =
            list.stream().collect(Collectors.groupingBy(CodeGeneratePageVO::getTableName));

        for (Map.Entry<String, List<CodeGenerateItemDTO>> item : groupMap.entrySet()) {

            // 处理并封装数据
            CodeGenerateDTO codeGenerateDTO = getCodeGenerateDTO(item);

            JSONObject json = JSONUtil.parseObj(codeGenerateDTO);

            generateSpringController(rootFileName, engine, codeGenerateDTO, json);

            generateSpringModel(rootFileName, engine, codeGenerateDTO, json);

            generateSpringServiceAndMapper(rootFileName, engine, codeGenerateDTO, json);

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 处理并封装数据
     */
    private CodeGenerateDTO getCodeGenerateDTO(Map.Entry<String, List<CodeGenerateItemDTO>> item) {

        String tableName = item.getKey();
        String tableComment = item.getValue().get(0).getTableComment();

        String tableNameCamelCase = StrUtil.toCamelCase(tableName);
        String tableNameCamelCaseUpperFirst = StrUtil.upperFirst(tableNameCamelCase);

        for (CodeGenerateItemDTO subItem : item.getValue()) {

            subItem.setColumnNameCamelCase(StrUtil.toCamelCase(subItem.getColumnName()));

            ColumnTypeRefEnum columnTypeRefEnum = ColumnTypeRefEnum.getByColumnType(subItem);

            if (columnTypeRefEnum != null) {
                subItem.setColumnJavaType(columnTypeRefEnum.getJavaType());
                subItem.setColumnTsType(columnTypeRefEnum.getTsType());
            }
        }

        // 获取：父类名
        String supperClassName = CodeGenerateHelperUtil.getSupperClassName(item.getValue());

        // 获取：没有父类字段 list
        List<CodeGenerateItemDTO> noSupperClassColumnList =
            CodeGenerateHelperUtil.getNoSupperClassColumnList(supperClassName, item.getValue());

        CodeGenerateDTO codeGenerateDTO = new CodeGenerateDTO();
        codeGenerateDTO.setTableName(tableName);
        codeGenerateDTO.setTableComment(tableComment);
        codeGenerateDTO.setTableNameCamelCase(tableNameCamelCase);
        codeGenerateDTO.setTableNameCamelCaseUpperFirst(tableNameCamelCaseUpperFirst);
        codeGenerateDTO.setColumnList(item.getValue());
        codeGenerateDTO.setSupperClassName(supperClassName);
        codeGenerateDTO.setNoSupperClassColumnList(noSupperClassColumnList);

        return codeGenerateDTO;
    }

    /**
     * 生成 spring-service
     */
    @SneakyThrows
    private void generateSpringServiceAndMapper(String rootFileName, TemplateEngine engine,
        CodeGenerateDTO codeGenerateDTO, JSONObject json) {

        Template template = engine.getTemplate("BaseService.java.ftl");

        File file = FileUtil
            .file(rootFileName + "/service/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "Service.java");
        FileUtil.touch(file);

        template.render(json, file);

        template = engine.getTemplate("BaseServiceImpl.java.ftl");

        file = FileUtil.file(
            rootFileName + "/service/impl/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "ServiceImpl.java");
        FileUtil.touch(file);

        template.render(json, file);

        template = engine.getTemplate("BaseMapper.java.ftl");

        file = FileUtil
            .file(rootFileName + "/mapper/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "Mapper.java");
        FileUtil.touch(file);

        template.render(json, file);

        template = engine.getTemplate("BaseMapper.xml.ftl");

        file =
            FileUtil.file(rootFileName + "/mapper/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "Mapper.xml");
        FileUtil.touch(file);

        template.render(json, file);

    }

    /**
     * 生成 spring-model
     */
    @SneakyThrows
    private void generateSpringModel(String rootFileName, TemplateEngine engine, CodeGenerateDTO codeGenerateDTO,
        JSONObject json) {

        Template template = engine.getTemplate("BaseDO.java.ftl");

        File file = FileUtil
            .file(rootFileName + "/model/entity/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "DO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // DO ↑

        template = engine.getTemplate("BaseInfoByIdVO.java.ftl");

        file = FileUtil
            .file(rootFileName + "/model/vo/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "InfoByIdVO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // InfoByIdVO ↑

        template = engine.getTemplate("BaseInsertOrUpdateDTO.java.ftl");

        file = FileUtil.file(rootFileName + "/model/dto/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst()
            + "InsertOrUpdateDTO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // InsertOrUpdateDTO ↑

        template = engine.getTemplate("BasePageDTO.java.ftl");

        file = FileUtil
            .file(rootFileName + "/model/dto/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "PageDTO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // PageDTO ↑

        template = engine.getTemplate("BasePageVO.java.ftl");

        file = FileUtil
            .file(rootFileName + "/model/vo/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "PageVO.java");
        FileUtil.touch(file);

        template.render(json, file);
        // PageVO ↑

    }

    /**
     * 生成 spring-controller
     */
    @SneakyThrows
    private void generateSpringController(String rootFileName, TemplateEngine engine, CodeGenerateDTO codeGenerateDTO,
        JSONObject json) {

        Template template = engine.getTemplate("BaseController.java.ftl");

        File file = FileUtil.file(
            rootFileName + "/controller/" + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "Controller.java");
        FileUtil.touch(file);

        template.render(json, file);
    }

    /**
     * 生成前端代码
     */
    @Override
    public String forAnt(List<CodeGenerateItemDTO> list) {

        String rootFileName = System.getProperty("user.dir") + "/src/main/java/generate/ant/";

        File rootFile = FileUtil.file(rootFileName);
        FileUtil.mkdir(rootFile); // 不存在则会创建，存在了则不进行操作

        TemplateEngine engine =
            TemplateUtil.createEngine(new TemplateConfig("ftl/ant", TemplateConfig.ResourceMode.CLASSPATH));

        Map<String, List<CodeGenerateItemDTO>> groupMap =
            list.stream().collect(Collectors.groupingBy(CodeGeneratePageVO::getTableName));

        for (Map.Entry<String, List<CodeGenerateItemDTO>> item : groupMap.entrySet()) {

            // 处理并封装数据
            CodeGenerateDTO codeGenerateDTO = getCodeGenerateDTO(item);

            JSONObject json = JSONUtil.parseObj(codeGenerateDTO);

            Template template = engine.getTemplate("Controller.ts.ftl");

            File file =
                FileUtil.file(rootFileName + codeGenerateDTO.getTableNameCamelCaseUpperFirst() + "Controller.ts");
            FileUtil.touch(file);

            template.render(json, file);

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

}
