package com.cmc.projectutil.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.model.dto.CodeGenerateForSpringListDTO;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;

import java.util.List;

public interface CodeGenerateService {

    Page<CodeGeneratePageVO> myPage(CodeGeneratePageDTO dto);

    String forSpring(List<CodeGenerateForSpringListDTO> list);

    String forAnt(List<CodeGenerateForSpringListDTO> list);

}
