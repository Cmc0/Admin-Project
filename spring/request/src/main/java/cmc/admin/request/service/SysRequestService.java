package cmc.admin.request.service;

import cmc.admin.request.model.dto.SysRequestPageDTO;
import cmc.admin.request.model.entity.SysRequestDO;
import cmc.admin.request.model.vo.SysRequestPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysRequestService extends IService<SysRequestDO> {

    Page<SysRequestPageVO> myPage(SysRequestPageDTO dto);

}
