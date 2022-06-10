package com.admin.common.configuration.mybatisplus;

import com.admin.common.util.UserUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MetaObjectHandlerConfiguration implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        Date date = new Date();
        Long currentUserIdSafe = UserUtil.getCurrentUserIdSafe();

        // 插入的实体类有值时，这里不会生效
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        this.strictInsertFill(metaObject, "createId", Long.class, currentUserIdSafe);
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateId", Long.class, currentUserIdSafe);
        this.strictInsertFill(metaObject, "version", Integer.class, 0);
        this.strictInsertFill(metaObject, "orderNo", Integer.class, 0);
        this.strictInsertFill(metaObject, "remark", String.class, "");
        this.strictInsertFill(metaObject, "enableFlag", Boolean.class, true);
        this.strictInsertFill(metaObject, "delFlag", Boolean.class, false);

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        // 插入的实体类有值时，这里不会生效
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateId", Long.class, UserUtil.getCurrentUserIdSafe());

    }

}
