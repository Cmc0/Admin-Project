package com.cmc.projectutil.service.impl;

import com.cmc.projectutil.model.dto.NotBlankStrDTO;
import com.cmc.projectutil.service.SqlToJavaService;
import org.springframework.stereotype.Service;

@Service
public class SqlToJavaServiceImpl implements SqlToJavaService {

    /**
     * sql转java
     */
    @Override
    public String sqlToJava(NotBlankStrDTO dto) {

        System.out.println(dto.getValue());

        return "2";
    }

}
