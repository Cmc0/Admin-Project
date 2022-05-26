package com.cmc.projectutil.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    private static final Long USER_ID = -1L;

    @Override
    public void insertFill(MetaObject metaObject) {

        Date date = new Date();

        // 插入的实体类有值时，这里不会生效
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        this.strictInsertFill(metaObject, "createId", Long.class, USER_ID);
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateId", Long.class, USER_ID);
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
        this.strictUpdateFill(metaObject, "updateId", Long.class, USER_ID);
    }

}
