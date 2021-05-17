-- ----------------------------
-- 2021年5月17日 13:50:09
-- kipf v2.7.2
-- gscheduler v1.1.0
-- Developer 易佳骆
-- ----------------------------
DROP TABLE IF EXISTS "developer_gs"."gscheduler_project_purview";
CREATE TABLE "developer_gs"."gscheduler_project_purview"
(
    "id"            serial8,
    "project_id"    int4,
    "user_group_id" varchar(255) COLLATE "pg_catalog"."default",
    "role_id"       varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "developer_gs"."gscheduler_project_purview"
    ADD CONSTRAINT "gscheduler_project_purview_pkey" PRIMARY KEY ("id");
-- ----------------------------
-- 【END】gscheduler
-- ----------------------------
