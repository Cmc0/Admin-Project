package com.admin.server.service.impl;

import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.admin.server.model.vo.ServerWorkInfoVO;
import com.admin.server.service.ServerService;
import org.springframework.stereotype.Service;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;

@Service
public class ServerServiceImpl implements ServerService {

    /**
     * 服务器运行情况
     */
    @Override
    public ServerWorkInfoVO workInfo() {

        ServerWorkInfoVO serverWorkInfoVO = new ServerWorkInfoVO();

        // jvm信息
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        serverWorkInfoVO.setJvmTotalMemory(runtimeInfo.getTotalMemory());
        serverWorkInfoVO.setJvmFreeMemory(runtimeInfo.getFreeMemory());
        serverWorkInfoVO.setJvmUsedMemory(serverWorkInfoVO.getJvmTotalMemory() - serverWorkInfoVO.getJvmFreeMemory());

        // 服务器内存信息
        GlobalMemory memory = OshiUtil.getMemory();
        serverWorkInfoVO.setMemoryTotal(memory.getTotal());
        serverWorkInfoVO.setMemoryAvailable(memory.getAvailable());
        serverWorkInfoVO.setMemoryUsed(memory.getTotal() - memory.getAvailable());

        // cpu信息
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        serverWorkInfoVO.setCpuUsed(cpuInfo.getUsed());

        // 磁盘信息
        long diskTotal = 0L;
        long diskUsable = 0L;
        OperatingSystem os = OshiUtil.getOs();
        List<OSFileStore> fileStoreList = os.getFileSystem().getFileStores();
        for (OSFileStore item : fileStoreList) {
            diskTotal += item.getTotalSpace();
            diskUsable += item.getUsableSpace();
        }
        serverWorkInfoVO.setDiskTotal(diskTotal);
        serverWorkInfoVO.setDiskUsable(diskUsable);
        serverWorkInfoVO.setDiskUsed(diskTotal - diskUsable);

        return serverWorkInfoVO;
    }
}
