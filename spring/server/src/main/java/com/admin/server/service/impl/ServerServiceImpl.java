package com.admin.server.service.impl;

import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.admin.server.model.vo.ServerWorkInfoVO;
import com.admin.server.service.ServerService;
import org.springframework.stereotype.Service;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;

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

        // 服务器内存信息
        GlobalMemory memory = OshiUtil.getMemory();
        serverWorkInfoVO.setMemoryTotal(memory.getTotal());
        serverWorkInfoVO.setMemoryAvailable(memory.getAvailable());

        // cpu信息
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        serverWorkInfoVO.setCpuTotal(cpuInfo.getToTal());

        long diskTotal = 0L;
        long diskUsing = 0L;

        List<HWDiskStore> diskStoreList = OshiUtil.getDiskStores();
        for (HWDiskStore item : diskStoreList) {
            diskTotal += item.getSize();
            diskUsing += item.getWriteBytes();
        }

        serverWorkInfoVO.setDiskTotal(diskTotal);
        serverWorkInfoVO.setDiskUsing(diskUsing);

        return serverWorkInfoVO;
    }
}
