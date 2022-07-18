CREATE DATABASE IF NOT EXISTS `admin-project` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use `admin-project`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_area
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `parent_id` bigint NOT NULL COMMENT '父节点id（顶级则为0）',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名',
                             `order_no` int NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '区域主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_area
-- ----------------------------

-- ----------------------------
-- Table structure for sys_area_ref_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_area_ref_dept`;
CREATE TABLE `sys_area_ref_dept`  (
                                      `area_id` bigint NOT NULL COMMENT '区域主键 id',
                                      `dept_id` bigint NOT NULL COMMENT '部门主键 id',
                                      PRIMARY KEY (`area_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '区域，部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_area_ref_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_bulletin
-- ----------------------------
DROP TABLE IF EXISTS `sys_bulletin`;
CREATE TABLE `sys_bulletin`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `create_id` bigint NOT NULL,
                                 `create_time` datetime NOT NULL,
                                 `update_id` bigint NOT NULL,
                                 `update_time` datetime NOT NULL,
                                 `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                                 `version` int NOT NULL COMMENT '乐观锁',
                                 `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                                 `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                                 `type` tinyint NOT NULL COMMENT '公告类型（字典值）',
                                 `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容（富文本）',
                                 `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
                                 `publish_time` datetime NOT NULL COMMENT '发布时间',
                                 `status` tinyint NOT NULL COMMENT '公告状态：1 草稿 2 公示',
                                 `xxl_job_id` bigint NOT NULL COMMENT 'xxlJobId',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_bulletin
-- ----------------------------

-- ----------------------------
-- Table structure for sys_bulletin_read_time_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_bulletin_read_time_ref_user`;
CREATE TABLE `sys_bulletin_read_time_ref_user`  (
                                                    `user_id` bigint NOT NULL COMMENT '用户主键 id',
                                                    `bulletin_read_time` datetime NOT NULL COMMENT '用户最近查看公告的时间，目的：统计公告数量时，根据这个时间和公告发布时间来过滤',
                                                    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公告，用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_bulletin_read_time_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `parent_id` bigint NOT NULL COMMENT '父节点id（顶级则为0）',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名',
                             `order_no` int NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dept_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_ref_user`;
CREATE TABLE `sys_dept_ref_user`  (
                                      `dept_id` bigint NOT NULL COMMENT '部门主键 id',
                                      `user_id` bigint NOT NULL COMMENT '用户主键 id',
                                      PRIMARY KEY (`dept_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门，用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `dict_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典 key（不能重复），字典项要冗余这个 key，目的：方便操作',
                             `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典/字典项 名',
                             `type` tinyint NOT NULL COMMENT '类型：1 字典 2 字典项',
                             `value` tinyint NOT NULL COMMENT '字典项 value（数字 123...）备注：字典为 -1',
                             `order_no` int NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, 1, '2022-03-07 16:13:14', 1, '2022-05-06 15:04:54', 1, 3, 0, '', 'request_category', '请求类别', 1, -1, 10000);
INSERT INTO `sys_dict` VALUES (2, 1, '2022-03-07 16:16:34', 1, '2022-05-06 15:04:59', 1, 3, 0, '', 'request_category', 'H5（网页端）', 2, 1, 10000);
INSERT INTO `sys_dict` VALUES (3, 1, '2022-03-07 16:16:50', 1, '2022-05-06 15:05:07', 1, 3, 0, '', 'request_category', 'APP（移动端）', 2, 2, 9990);
INSERT INTO `sys_dict` VALUES (4, 1, '2022-03-07 16:17:01', 1, '2022-05-06 15:05:15', 1, 3, 0, '', 'request_category', 'PC（桌面程序）', 2, 3, 9980);
INSERT INTO `sys_dict` VALUES (5, 1, '2022-04-13 21:47:07', 1, '2022-05-06 15:05:22', 1, 1, 0, '', 'request_category', '微信小程序', 2, 4, 9970);
INSERT INTO `sys_dict` VALUES (6, 1, '2022-05-07 09:57:06', 1, '2022-05-07 09:57:06', 1, 1, 0, '', 'bulletin_type', '公告类型', 1, -1, 9990);
INSERT INTO `sys_dict` VALUES (7, 1, '2022-05-07 09:57:34', 1, '2022-05-07 09:58:43', 1, 0, 0, '', 'bulletin_type', '系统升级', 2, 1, 10000);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件原始名（包含文件类型）',
                             `file_ext_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型，备注：这个是读取文件流的头部信息获得文件类型',
                             `extra_json` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '额外信息（json格式）',
                             `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件 url（包含文件类型）',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件上传记录主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `create_id` bigint NOT NULL,
                            `create_time` datetime NOT NULL,
                            `update_id` bigint NOT NULL,
                            `update_time` datetime NOT NULL,
                            `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                            `version` int NOT NULL COMMENT '乐观锁',
                            `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                            `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                            `parent_id` bigint NOT NULL COMMENT '父节点id（顶级则为0）',
                            `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名',
                            `order_no` int NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_ref_user`;
CREATE TABLE `sys_job_ref_user`  (
                                     `job_id` bigint NOT NULL COMMENT '岗位主键 id',
                                     `user_id` bigint NOT NULL COMMENT '用户主键 id',
                                     PRIMARY KEY (`job_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位，用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名',
                             `path` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '页面的 path，备注：相同父菜单下，子菜单 path不能重复',
                             `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标',
                             `parent_id` bigint NOT NULL COMMENT '父节点id（顶级则为0）',
                             `auths` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById',
                             `show_flag` tinyint(1) NOT NULL COMMENT '是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到',
                             `link_flag` tinyint(1) NOT NULL COMMENT '是否外链，即，打开页面会在一个新的窗口打开，可以配合 router',
                             `router` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路由',
                             `redirect` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '重定向，暂时未使用',
                             `order_no` int NOT NULL COMMENT '排序号（值越大越前面，默认为 0）',
                             `first_flag` tinyint(1) NOT NULL COMMENT '是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单',
                             `auth_flag` tinyint(1) NOT NULL COMMENT '是否是权限菜单，权限菜单：不显示，只代表菜单权限',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 469 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 1, '2021-12-22 10:42:43', 1, '2022-06-23 16:36:59', 1, 0, 0, '', '仪表板', '', 'DashboardOutlined', 0, '', 1, 0, '', '', 10000, 0, 0);
INSERT INTO `sys_menu` VALUES (2, 1, '2021-12-21 12:31:52', 1, '2022-05-21 02:30:08', 1, 0, 0, '', '工作台', '/main/dashboard/workplace', '', 1, '', 1, 0, 'dashboardWorkplaceWorkplace', '', 10000, 1, 0);
INSERT INTO `sys_menu` VALUES (3, 1, '2021-12-22 10:53:32', 1, '2022-06-23 16:36:39', 1, 0, 0, '', '系统管理', '', 'SettingOutlined', 0, '', 1, 0, '', '', 9990, 0, 0);
INSERT INTO `sys_menu` VALUES (4, 1, '2021-12-20 11:10:15', 1, '2022-05-03 16:21:50', 1, 0, 0, '', '菜单管理', '/main/sys/menu', '', 3, '', 1, 0, 'sysMenuMenu', '', 10000, 0, 0);
INSERT INTO `sys_menu` VALUES (5, 1, '2022-05-21 01:00:18', 1, '2022-05-21 01:00:18', 1, 0, 0, '', '新增修改', '', '', 4, 'sysMenu:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (6, 1, '2022-05-21 01:00:18', 1, '2022-05-21 01:00:18', 1, 0, 0, '', '列表查询', '', '', 4, 'sysMenu:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (7, 1, '2022-05-21 01:00:19', 1, '2022-05-21 01:00:19', 1, 0, 0, '', '删除', '', '', 4, 'sysMenu:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (8, 1, '2022-05-21 01:00:19', 1, '2022-05-21 01:00:19', 1, 0, 0, '', '查看详情', '', '', 4, 'sysMenu:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (9, 1, '2021-12-22 11:09:35', 0, '2022-07-18 02:56:28', 1, 0, 0, '', '角色管理', '/main/sys/role', '', 3, '', 1, 0, 'sysRoleRole', '', 9990, 0, 0);
INSERT INTO `sys_menu` VALUES (10, 1, '2022-05-21 01:03:25', 1, '2022-05-21 01:03:25', 1, 0, 0, '', '新增修改', '', '', 9, 'sysRole:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (11, 1, '2022-05-21 01:03:26', 1, '2022-05-21 01:03:26', 1, 0, 0, '', '列表查询', '', '', 9, 'sysRole:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (12, 1, '2022-05-21 01:03:26', 1, '2022-05-21 01:03:26', 1, 0, 0, '', '删除', '', '', 9, 'sysRole:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (13, 1, '2022-05-21 01:03:26', 1, '2022-05-21 01:03:26', 1, 0, 0, '', '查看详情', '', '', 9, 'sysRole:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (14, 1, '2021-12-22 11:09:16', 1, '2022-05-03 17:00:27', 1, 0, 0, '', '用户管理', '/main/sys/user', '', 3, '', 1, 0, 'sysUserUser', '', 9980, 0, 0);
INSERT INTO `sys_menu` VALUES (15, 1, '2022-05-21 01:03:16', 1, '2022-05-21 01:03:16', 1, 0, 0, '', '新增修改', '', '', 14, 'sysUser:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (16, 1, '2022-05-21 01:03:17', 1, '2022-05-21 01:03:17', 1, 0, 0, '', '列表查询', '', '', 14, 'sysUser:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (17, 1, '2022-05-21 01:03:17', 1, '2022-05-21 01:03:17', 1, 0, 0, '', '删除', '', '', 14, 'sysUser:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (18, 1, '2022-05-21 01:03:17', 1, '2022-05-21 01:03:17', 1, 0, 0, '', '查看详情', '', '', 14, 'sysUser:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (19, 1, '2021-12-22 11:09:53', 1, '2022-07-03 14:12:10', 1, 0, 0, '', '字典管理', '/main/sys/dict', '', 3, '', 1, 0, 'sysDictDict', '', 9970, 0, 0);
INSERT INTO `sys_menu` VALUES (20, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 0, 0, '', '新增修改', '', '', 19, 'sysDict:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (21, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 0, 0, '', '列表查询', '', '', 19, 'sysDict:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (22, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 0, 0, '', '删除', '', '', 19, 'sysDict:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (23, 1, '2022-05-21 01:03:32', 1, '2022-05-21 01:03:32', 1, 0, 0, '', '查看详情', '', '', 19, 'sysDict:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (24, 1, '2021-12-22 11:10:31', 1, '2022-07-03 14:12:01', 1, 0, 0, '', '系统参数', '/main/sys/param', '', 3, '', 1, 0, 'sysParamParam', '', 9960, 0, 0);
INSERT INTO `sys_menu` VALUES (25, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 0, 0, '', '新增修改', '', '', 24, 'sysParam:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (26, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 0, 0, '', '列表查询', '', '', 24, 'sysParam:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (27, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 0, 0, '', '删除', '', '', 24, 'sysParam:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (28, 1, '2022-05-21 01:03:57', 1, '2022-05-21 01:03:57', 1, 0, 0, '', '查看详情', '', '', 24, 'sysParam:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (29, 1, '2021-12-22 11:10:05', 1, '2022-05-03 17:01:13', 1, 0, 0, '', '部门管理', '/main/sys/dept', '', 3, '', 1, 0, 'sysDeptDept', '', 9950, 0, 0);
INSERT INTO `sys_menu` VALUES (30, 1, '2022-05-21 01:03:39', 1, '2022-05-21 01:03:39', 1, 0, 0, '', '新增修改', '', '', 29, 'sysDept:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (31, 1, '2022-05-21 01:03:39', 1, '2022-05-21 01:03:39', 1, 0, 0, '', '列表查询', '', '', 29, 'sysDept:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (32, 1, '2022-05-21 01:03:39', 1, '2022-05-21 01:03:39', 1, 0, 0, '', '删除', '', '', 29, 'sysDept:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (33, 1, '2022-05-21 01:03:39', 1, '2022-05-21 01:03:39', 1, 0, 0, '', '查看详情', '', '', 29, 'sysDept:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (34, 1, '2021-12-22 11:10:12', 1, '2022-05-03 17:01:21', 1, 0, 0, '', '区域管理', '/main/sys/area', '', 3, '', 1, 0, 'sysAreaArea', '', 9940, 0, 0);
INSERT INTO `sys_menu` VALUES (35, 1, '2022-05-21 01:03:45', 1, '2022-05-21 01:03:45', 1, 0, 0, '', '新增修改', '', '', 34, 'sysArea:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (36, 1, '2022-05-21 01:03:45', 1, '2022-05-21 01:03:45', 1, 0, 0, '', '列表查询', '', '', 34, 'sysArea:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (37, 1, '2022-05-21 01:03:45', 1, '2022-05-21 01:03:45', 1, 0, 0, '', '删除', '', '', 34, 'sysArea:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (38, 1, '2022-05-21 01:03:45', 1, '2022-05-21 01:03:45', 1, 0, 0, '', '查看详情', '', '', 34, 'sysArea:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (39, 1, '2021-12-22 11:10:22', 1, '2022-05-03 17:01:28', 1, 0, 0, '', '岗位管理', '/main/sys/job', '', 3, '', 1, 0, 'sysJobJob', '', 9930, 0, 0);
INSERT INTO `sys_menu` VALUES (40, 1, '2022-05-21 01:03:51', 1, '2022-05-21 01:03:51', 1, 0, 0, '', '新增修改', '', '', 39, 'sysJob:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (41, 1, '2022-05-21 01:03:51', 1, '2022-05-21 01:03:51', 1, 0, 0, '', '列表查询', '', '', 39, 'sysJob:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (42, 1, '2022-05-21 01:03:51', 1, '2022-05-21 01:03:51', 1, 0, 0, '', '删除', '', '', 39, 'sysJob:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (43, 1, '2022-05-21 01:03:51', 1, '2022-05-21 01:03:51', 1, 0, 0, '', '查看详情', '', '', 39, 'sysJob:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (44, 1, '2022-07-11 14:26:29', 1, '2022-07-11 14:27:50', 1, 0, 0, '', '公告管理', '/main/sys/bulletin', '', 3, '', 1, 0, 'sysBulletinBulletin', '', 9920, 0, 0);
INSERT INTO `sys_menu` VALUES (45, 1, '2022-07-11 14:29:11', 1, '2022-07-11 14:29:11', 1, 0, 0, '', '新增修改', '', '', 44, 'sysBulletin:insertOrUpdate', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (46, 1, '2022-07-11 14:29:12', 1, '2022-07-11 14:29:12', 1, 0, 0, '', '列表查询', '', '', 44, 'sysBulletin:page', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (47, 1, '2022-07-11 14:29:13', 1, '2022-07-11 14:29:13', 1, 0, 0, '', '删除', '', '', 44, 'sysBulletin:deleteByIdSet', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (48, 1, '2022-07-11 14:29:13', 1, '2022-07-11 14:29:13', 1, 0, 0, '', '查看详情', '', '', 44, 'sysBulletin:infoById', 0, 0, '', '', 9970, 0, 1);
INSERT INTO `sys_menu` VALUES (49, 1, '2021-12-22 11:08:06', 1, '2022-06-23 16:41:26', 1, 0, 0, '', '系统监控', '', 'LaptopOutlined', 0, '', 1, 0, '', '', 9980, 0, 0);
INSERT INTO `sys_menu` VALUES (50, 1, '2021-12-22 11:11:36', 1, '2022-06-23 11:16:33', 1, 0, 0, '', '在线用户', '/main/sysMonitor/onlineUser', '', 49, '', 1, 0, 'sysMonitorOnlineUserOnlineUser', '', 10000, 0, 0);
INSERT INTO `sys_menu` VALUES (51, 1, '2021-12-22 11:11:47', 1, '2022-06-23 11:17:01', 1, 0, 0, '', '请求监控', '/main/sysMonitor/request', '', 49, '', 1, 0, 'sysMonitorRequestRequest', '', 9990, 0, 0);
INSERT INTO `sys_menu` VALUES (52, 1, '2022-05-03 19:54:50', 1, '2022-05-03 19:56:10', 1, 0, 0, '', '强退', '', '', 50, 'sysWebSocket:insertOrUpdate', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (53, 1, '2022-05-03 17:33:43', 1, '2022-05-03 17:33:43', 1, 0, 0, '', '列表查询', '', '', 50, 'sysWebSocket:page', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (54, 1, '2022-05-03 17:34:10', 1, '2022-05-03 17:34:23', 1, 0, 0, '', '列表查询', '', '', 51, 'sysRequest:page', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (55, 1, '2021-12-24 11:37:27', 1, '2021-12-24 11:43:29', 1, 0, 0, '', '个人管理', '', '', 0, '', 0, 0, '', '', 9970, 0, 0);
INSERT INTO `sys_menu` VALUES (56, 1, '2021-12-24 11:37:42', 1, '2022-07-04 11:31:55', 1, 0, 0, '', '个人中心', '/main/user/self', '', 55, '', 1, 0, 'userSelfSelf', '', 10000, 0, 0);
INSERT INTO `sys_menu` VALUES (57, 1, '2022-03-31 09:49:00', 1, '2022-03-31 14:16:35', 1, 0, 0, '', '消息通知', '', '', 0, '', 0, 0, '', '', 9960, 0, 0);
INSERT INTO `sys_menu` VALUES (58, 1, '2022-05-07 14:41:00', 1, '2022-05-07 14:45:01', 1, 0, 0, '', '公告', '/main/message/bulletin', '', 57, '', 1, 0, 'messageBulletinBulletin', '', 10000, 0, 0);
INSERT INTO `sys_menu` VALUES (59, 1, '2022-03-31 09:57:38', 1, '2022-05-07 14:40:01', 1, 0, 0, '', '消息', '/main/message/message', '', 57, '', 1, 0, 'messageMessageMessage', '', 9990, 0, 0);
INSERT INTO `sys_menu` VALUES (60, 1, '2022-07-08 22:22:03', 1, '2022-07-08 22:22:03', 1, 0, 0, '', '服务器运行情况', '', '', 2, 'server:workInfo', 0, 0, '', '', 10000, 0, 1);
INSERT INTO `sys_menu` VALUES (61, 1, '2022-07-08 22:22:35', 1, '2022-07-08 22:22:35', 1, 0, 0, '', '平台系统-分析-请求', '', '', 2, 'systemAnalyze:requset', 0, 0, '', '', 9990, 0, 1);
INSERT INTO `sys_menu` VALUES (62, 1, '2022-07-08 22:22:51', 1, '2022-07-08 22:22:51', 1, 0, 0, '', '平台系统-分析-用户', '', '', 2, 'systemAnalyze:user', 0, 0, '', '', 9980, 0, 1);
INSERT INTO `sys_menu` VALUES (63, 1, '2022-07-12 11:01:41', 1, '2022-07-12 11:02:01', 1, 0, 0, '', 'Helper App', 'https://cmc0.github.io/', 'CodeOutlined', 0, '', 1, 1, '', '', 0, 0, 0);
INSERT INTO `sys_menu` VALUES (64, 1, '2022-07-15 15:19:32', 1, '2022-07-15 15:19:32', 1, 0, 0, '', 'Elastic', 'http://81.69.58.190:5601/', 'ReadOutlined', 0, '', 1, 1, '', '', 0, 0, 0);
INSERT INTO `sys_menu` VALUES (65, 1, '2021-12-22 11:05:23', 1, '2022-07-07 21:06:42', 1, 0, 0, '', '文件服务', 'http://139.196.121.3:9001', 'FolderOpenOutlined', 0, '', 1, 1, '', '', 0, 0, 0);
INSERT INTO `sys_menu` VALUES (66, 1, '2021-12-22 11:04:05', 1, '2022-06-24 09:30:18', 1, 0, 0, '', '定时任务', 'http://139.196.121.3:8335/xxl-job-admin', 'FieldTimeOutlined', 0, '', 1, 1, '', '', 0, 0, 0);

-- ----------------------------
-- Table structure for sys_param
-- ----------------------------
DROP TABLE IF EXISTS `sys_param`;
CREATE TABLE `sys_param`  (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `create_id` bigint NOT NULL,
                              `create_time` datetime NOT NULL,
                              `update_id` bigint NOT NULL,
                              `update_time` datetime NOT NULL,
                              `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                              `version` int NOT NULL COMMENT '乐观锁',
                              `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                              `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名，以 id为不变值进行使用，不要用此属性',
                              `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '值',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统参数主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_param
-- ----------------------------
INSERT INTO `sys_param` VALUES (1, 1, '2021-12-26 11:32:38', 1, '2022-01-11 21:03:30', 1, 0, 0, '获取私钥方法：new RSA().getPrivateKeyBase64()，备注：获取公钥同理，但是必须使用同一个对象，因为必须成对', '非对称加密，私钥', 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANp2ZoJp983rckLlCwwr/6V7m+gIHgPM6xX3E4FmAYnKqeLOH39RwWQihFp7mt5t1ZS2c87gJOB0lu5K+79O0gg+P0N7n/CImKUUBZ3VV7uoZrHFPGpbAU2nun8UuvRikx4GM7KV2z0bIDVyeMiqu411i/6pFmex4TRKrZYRm6z1AgMBAAECgYAMjU//f0IvMS97+3gCh4alRfBjyQ+cbUo2lV8oCKne9meDcg9qO9LOQ5NyNXbk/8+NP1xxDvzfbqN7ZpCHYep8VoxJYMqr15czK9Sk34A5AdpOb5kQhUAgfyaQlIu+2s3NSjyJUXcNqLRRb0xiGhoJmH1V9zGSVFaJnGsUJuZAkwJBAPl/4VNtvcWTyoGBHFlJjto4V1lYkD63qKh66evXiI7PEQhGB4b8ubBFnEJephWQO/tWo6AYFipMrtjJ1z+KqWsCQQDgJ4IsRZleS5vr5bYhL5+YE8BN8TyzyJ/7MvSjV6ZB7Qoq+w7CSsWm4wTnO9zdSuJaXJ7QmMfDR9Y/tAx2MLsfAkBkdJOxtqbI7VeEywox/QbyX+rzg1AYoHPc2hhjJ9XIwiB2d1PCivDswypGIru2ROuRp/GbnPcXsuZXTPVIlTjfAkEAkhtukCj1pS8nfQYIR21hW6FUMfnSlWVqUjSOnYHeTw6RGB75Kc/PMc68PXUZq+zJyhihNFrBqxpCHtffX5K4BQJAIIs70dCXBmZ1AjWUTgRY1piEGtwoxPO229guHvF6P8IOyxCuFWrCgY/1UnZt3Yc/XubImBb/xQx5CcdFWYcgjw==');
INSERT INTO `sys_param` VALUES (2, 1, '2021-12-26 11:32:38', 1, '2022-06-28 16:31:42', 1, 0, 0, '多少秒钟，一个 ip可以请求多少次，用冒号隔开的，任意值小于等于 0，则不会进行检查，超过了，则一天无法访问任何接口', 'ip请求速率', '10:75');

-- ----------------------------
-- Table structure for sys_request
-- ----------------------------
DROP TABLE IF EXISTS `sys_request`;
CREATE TABLE `sys_request`  (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `create_id` bigint NOT NULL,
                                `create_time` datetime NOT NULL,
                                `update_id` bigint NOT NULL,
                                `update_time` datetime NOT NULL,
                                `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                                `version` int NOT NULL COMMENT '乐观锁',
                                `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                                `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                                `uri` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求的uri',
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接口名（备用）',
                                `time_str` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '耗时（字符串）',
                                `time_number` bigint NOT NULL COMMENT '耗时（毫秒）',
                                `category` tinyint NOT NULL COMMENT '类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序',
                                `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IpUtil.getRegion() 获取到的 ip所处区域',
                                `ip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ip',
                                `success_flag` tinyint(1) NOT NULL COMMENT '请求是否成功',
                                `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '失败信息',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '接口请求记录主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_request
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '启用/禁用',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名（不能重复）',
                             `default_flag` tinyint(1) NOT NULL COMMENT '是否是默认角色，备注：只会有一个默认角色',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 1, '2021-10-01 12:45:57', 1, '2022-07-12 11:02:33', 1, 0, 0, '', '超级管理员', 0);
INSERT INTO `sys_role` VALUES (2, 1, '2022-05-03 19:44:49', 1, '2022-07-14 14:24:49', 1, 0, 0, '', '默认角色', 1);

-- ----------------------------
-- Table structure for sys_role_ref_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_ref_menu`;
CREATE TABLE `sys_role_ref_menu`  (
                                      `role_id` bigint NOT NULL COMMENT '角色id',
                                      `menu_id` bigint NOT NULL COMMENT '菜单id',
                                      PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色，菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_ref_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_ref_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_ref_user`;
CREATE TABLE `sys_role_ref_user`  (
                                      `role_id` bigint NOT NULL COMMENT '角色id',
                                      `user_id` bigint NOT NULL COMMENT '用户id',
                                      PRIMARY KEY (`role_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色，用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_ref_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_id` bigint NOT NULL,
                             `create_time` datetime NOT NULL,
                             `update_id` bigint NOT NULL,
                             `update_time` datetime NOT NULL,
                             `enable_flag` tinyint(1) NOT NULL COMMENT '正常/冻结',
                             `version` int NOT NULL COMMENT '乐观锁',
                             `del_flag` tinyint(1) NOT NULL COMMENT '是否注销',
                             `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                             `uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '该用户的 uuid，本系统使用 id，不使用 uuid',
                             `jwt_secret_suf` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 jwt私钥后缀（simple uuid）',
                             `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
                             `bio` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '个人简介',
                             `avatar_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '头像url',
                             `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】',
                             `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_ref_wx
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_ref_wx`;
CREATE TABLE `sys_user_ref_wx`  (
                                    `user_id` bigint NOT NULL COMMENT '本系统用户主键 id',
                                    `open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信的 openId',
                                    PRIMARY KEY (`user_id`, `open_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户，微信关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_ref_wx
-- ----------------------------

-- ----------------------------
-- Table structure for sys_web_socket
-- ----------------------------
DROP TABLE IF EXISTS `sys_web_socket`;
CREATE TABLE `sys_web_socket`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `create_id` bigint NOT NULL,
                                   `create_time` datetime NOT NULL,
                                   `update_id` bigint NOT NULL,
                                   `update_time` datetime NOT NULL,
                                   `enable_flag` tinyint(1) NOT NULL COMMENT '连接中/断开连接',
                                   `version` int NOT NULL COMMENT '乐观锁',
                                   `del_flag` tinyint(1) NOT NULL COMMENT '是否逻辑删除',
                                   `remark` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述/备注',
                                   `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IpUtil.getRegion() 获取到的 ip所处区域',
                                   `ip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ip',
                                   `browser` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器和浏览器版本，用 / 分隔表示',
                                   `os` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作系统',
                                   `mobile_flag` tinyint(1) NOT NULL COMMENT '是否是移动端网页，true 是 false 否',
                                   `type` tinyint NOT NULL COMMENT '状态：1 在线 2 隐身',
                                   `server` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '本次 WebSocket 连接的服务器的 ip:port',
                                   `category` tinyint NOT NULL COMMENT '类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序',
                                   `jwt_hash` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jwtHash，用于匹配 redis中存储的 jwtHash',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'WebSocket 连接记录主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_web_socket
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
