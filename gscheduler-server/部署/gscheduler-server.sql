-- ----------------------------
-- Table structure for gscheduler_project_purview
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

-- ----------------------------
-- Primary Key structure for table gscheduler_project_purview
-- ----------------------------
ALTER TABLE "developer_gs"."gscheduler_project_purview"
    ADD CONSTRAINT "gscheduler_project_purview_pkey" PRIMARY KEY ("id");
