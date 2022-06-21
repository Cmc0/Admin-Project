package com.admin.xxljob.service;

import com.admin.common.model.dto.NotNullId;
import com.admin.xxljob.dto.XxlJobInsertOrUpdateDTO;

public interface XxlJobService {

    Long insertOrUpdate(XxlJobInsertOrUpdateDTO dto);

    void deleteById(NotNullId notNullId);

}
