package com.cmc.projectutil.service;

import com.cmc.projectutil.model.dto.NotBlankStrDTO;

public interface MyConvertService {

    String sqlToJava(NotBlankStrDTO dto);

    String javaToTs(NotBlankStrDTO dto);

    String sqlAddAs(NotBlankStrDTO dto);

    String forSpringByTableSql(NotBlankStrDTO dto);

    String forAntByTableSql(NotBlankStrDTO dto);
}
