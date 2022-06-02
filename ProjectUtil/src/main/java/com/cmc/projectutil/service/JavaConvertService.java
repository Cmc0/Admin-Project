package com.cmc.projectutil.service;

import com.cmc.projectutil.model.dto.NotBlankStrDTO;

public interface JavaConvertService {

    String sqlToJava(NotBlankStrDTO dto);

    String javaToTs(NotBlankStrDTO dto);

    String sqlAddAs(NotBlankStrDTO dto);
}
