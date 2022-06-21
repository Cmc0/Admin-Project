package cmc.admin.request.service.impl;

import cmc.admin.request.mapper.SysRequestMapper;
import cmc.admin.request.model.dto.SysRequestPageDTO;
import cmc.admin.request.model.entity.SysRequestDO;
import cmc.admin.request.model.vo.SysRequestPageVO;
import cmc.admin.request.service.SysRequestService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysRequestServiceImpl extends ServiceImpl<SysRequestMapper, SysRequestDO> implements SysRequestService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysRequestPageVO> myPage(SysRequestPageDTO dto) {
        return baseMapper.myPage(dto.getPage(), dto);
    }
}
