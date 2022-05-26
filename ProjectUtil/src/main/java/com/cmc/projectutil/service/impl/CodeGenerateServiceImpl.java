package com.cmc.projectutil.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.mapper.CodeGenerateMapper;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import com.cmc.projectutil.service.CodeGenerateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Override
    public String codeGenerateForSpring(List<CodeGeneratePageVO> list) {

        Map<String, List<CodeGeneratePageVO>> groupMap =
            list.stream().collect(Collectors.groupingBy(CodeGeneratePageVO::getTableName));

        for (Map.Entry<String, List<CodeGeneratePageVO>> item : groupMap.entrySet()) {

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}
