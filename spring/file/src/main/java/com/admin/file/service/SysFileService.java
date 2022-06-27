package com.admin.file.service;

import com.admin.file.model.dto.SysFileDownloadDTO;
import com.admin.file.model.dto.SysFileRemoveDTO;
import com.admin.file.model.dto.SysFileUploadDTO;
import com.admin.file.model.entity.SysFileDO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysFileService extends IService<SysFileDO> {

    String upload(SysFileUploadDTO dto);

    void publicDownload(String url);

    void download(SysFileDownloadDTO dto);

    String remove(SysFileRemoveDTO dto, boolean selfFlag);

}
