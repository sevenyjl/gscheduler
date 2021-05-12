-- ----------------------------
-- 【START gscheduler-triiger kipf-2.7.2 gscheduler-triiger-v1.1.0】
-- 2021年5月12日 10:17:21
-- ----------------------------
DROP TABLE IF EXISTS "developer_gs"."gscheduler_trigger";
CREATE TABLE "developer_gs"."gscheduler_trigger" (
  "id" int4 NOT NULL DEFAULT nextval('"developer_gs".g_scheduler_all_seq'::regclass),
  "task_id" varchar COLLATE "pg_catalog"."default",
  "corn" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" date,
  "update_time" date,
  "start_time" date,
  "end_time" date,
  "params" varchar(255) COLLATE "pg_catalog"."default",
  "type" varchar(255) COLLATE "pg_catalog"."default",
  "group_name" varchar(255) COLLATE "pg_catalog"."default",
  "del_falg" bool
)
;

-- ----------------------------
-- Uniques structure for table gscheduler_trigger
-- ----------------------------
ALTER TABLE "developer_gs"."gscheduler_trigger" ADD CONSTRAINT "gscheduler_trigger_task_id_group_name_key" UNIQUE ("task_id", "group_name");

-- ----------------------------
-- Primary Key structure for table gscheduler_trigger
-- ----------------------------
ALTER TABLE "developer_gs"."gscheduler_trigger" ADD CONSTRAINT "gscheduler_trigger_pkey" PRIMARY KEY ("id");
