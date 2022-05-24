package com.cmc.projectutil.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.mapper.CodeGenerateMapper;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import com.cmc.projectutil.service.CodeGenerateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
