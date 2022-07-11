package com.admin.bulletin.task;

import cn.hutool.core.thread.ThreadUtil;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.util.KafkaUtil;
import com.admin.xxljob.service.XxlJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BulletinPublishTask {

    public final static String BULLETIN_PUBLISH = "bulletinPublish";

    @Resource
    XxlJobService xxlJobService;

    @XxlJob(value = BULLETIN_PUBLISH)
    public void bulletinPublish() {

        long jobId = XxlJobHelper.getJobId();

        // 通知所有人，刷新 公告信息
        KafkaUtil.bulletinPublish(null);

        XxlJobHelper.handleSuccess("公告发布成功");

        // 异步删除任务
        ThreadUtil.execute(() -> xxlJobService.deleteById(new NotNullId(jobId)));
    }

}
