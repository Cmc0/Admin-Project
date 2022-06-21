package cmc.admin.request.mapper;

import cmc.admin.request.model.dto.SysRequestPageDTO;
import cmc.admin.request.model.entity.SysRequestDO;
import cmc.admin.request.model.vo.SysRequestPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface SysRequestMapper extends BaseMapper<SysRequestDO> {

    // 分页排序查询
    Page<SysRequestPageVO> myPage(Page<SysRequestPageVO> page, @Param("dto") SysRequestPageDTO dto);
}
