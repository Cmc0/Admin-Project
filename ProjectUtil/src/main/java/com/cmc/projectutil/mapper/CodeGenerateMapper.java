package com.cmc.projectutil.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.projectutil.model.dto.CodeGeneratePageDTO;
import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import org.apache.ibatis.annotations.Param;

public interface CodeGenerateMapper {

    // 分页排序查询
    Page<CodeGeneratePageVO> myPage(Page<CodeGeneratePageVO> page, @Param("dto") CodeGeneratePageDTO dto);
}
