package com.cmc.xxljob.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProgramIsRunning {

    @XxlJob("programIsRunning")
    public void programIsRunning() {
        XxlJobHelper.handleSuccess("项目运行中 (*^▽^*)");
    }

}
