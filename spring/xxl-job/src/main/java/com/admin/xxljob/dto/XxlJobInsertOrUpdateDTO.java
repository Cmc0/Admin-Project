package com.admin.xxljob.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class XxlJobInsertOrUpdateDTO {

    @ApiModelProperty(value = "任务描述")
    private String jobDesc;

    @ApiModelProperty(value = "发送时间（本系统定义），会转换为 Cron 表达式，并赋值给 scheduleConf")
    private Date proSendTime;

    @ApiModelProperty(value = "JobHandler")
    private String executorHandler;

    @ApiModelProperty(value = "任务参数，注意：这里必须是 json格式")
    private String executorParam = "{}";

    // 分割线，上面是建议必须设置的值 ↑ 下面是一般情况都不会填的值 ↓ =========

    @ApiModelProperty(value = "任务 id，备注：新增时不要设值，用于启动 任务时用")
    private Integer id;

    @ApiModelProperty(value = "执行器 id，备注：不指定时，自动获取第一个，如果一个都没有，则会自动创建")
    private String jobGroup;

    @ApiModelProperty(value = "负责人")
    private String author = "admin";

    @ApiModelProperty(value = "报警邮件")
    private String alarmEmail;

    @ApiModelProperty(value = "调度类型")
    private String scheduleType = "CRON";

    @ApiModelProperty(value = "Cron 表达式")
    private String scheduleConf;

    @ApiModelProperty(value = "运行模式")
    private String glueType = "BEAN";

    @ApiModelProperty(value = "路由策略")
    private String executorRouteStrategy = "FAILOVER"; // FAILOVER 故障转移

    @ApiModelProperty(value = "调度过期策略")
    private String misfireStrategy = "FIRE_ONCE_NOW"; // DO_NOTHING 忽略 FIRE_ONCE_NOW 立即执行一次

    /**
     * {@link com.xxl.job.core.enums.ExecutorBlockStrategyEnum}
     */
    @ApiModelProperty(value = "阻塞处理策略")
    private String executorBlockStrategy = "SERIAL_EXECUTION"; // SERIAL_EXECUTION 单机串行

    @ApiModelProperty(value = "任务超时时间，单位 秒，大于零时生效")
    private int executorTimeout;

    @ApiModelProperty(value = "失败重试次数")
    private int executorFailRetryCount;

    @ApiModelProperty(value = "不用设值", hidden = true)
    private String glueRemark = "GLUE代码初始化";

}
