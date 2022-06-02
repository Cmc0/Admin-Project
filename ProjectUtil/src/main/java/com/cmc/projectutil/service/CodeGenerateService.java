package com.cmc.projectutil.service;

import com.cmc.projectutil.model.dto.CodeGenerateItemDTO;

import java.util.List;

public interface CodeGenerateService {

    String forSpring(List<CodeGenerateItemDTO> list);

    String forAnt(List<CodeGenerateItemDTO> list);

}
