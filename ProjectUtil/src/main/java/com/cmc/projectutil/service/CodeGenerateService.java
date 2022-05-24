package com.cmc.projectutil.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;

public interface CodeGenerateService {

    Page<CodeGeneratePageVO> myPage(CodeGeneratePageDTO dto);
}
