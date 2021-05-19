-- ----------------------------
-- Table structure for t_ds_project
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_ds_project";
CREATE TABLE "public"."t_ds_project"
(
    "id"          int4 NOT NULL                               DEFAULT nextval('t_ds_project_id_sequence'::regclass),
    "name"        varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "description" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "user_id"     int4,
    "flag"        int4                                        DEFAULT 1,
    "create_time" timestamp(6)                                DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp(6)                                DEFAULT CURRENT_TIMESTAMP,
    "creator"     varchar(255) COLLATE "pg_catalog"."default",
    "updater"     varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for t_ds_process_definition
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_ds_process_definition";
CREATE TABLE "public"."t_ds_process_definition"
(
    "id"                      int4 NOT NULL                               DEFAULT nextval('t_ds_process_definition_id_sequence'::regclass),
    "name"                    varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "version"                 int4,
    "release_state"           int4,
    "project_id"              int4,
    "user_id"                 int4,
    "process_definition_json" text COLLATE "pg_catalog"."default",
    "description"             text COLLATE "pg_catalog"."default",
    "global_params"           text COLLATE "pg_catalog"."default",
    "flag"                    int4,
    "locations"               text COLLATE "pg_catalog"."default",
    "connects"                text COLLATE "pg_catalog"."default",
    "receivers"               text COLLATE "pg_catalog"."default",
    "receivers_cc"            text COLLATE "pg_catalog"."default",
    "create_time"             timestamp(6),
    "timeout"                 int4                                        DEFAULT 0,
    "tenant_id"               int4 NOT NULL                               DEFAULT '-1':: integer,
    "update_time"             timestamp(6),
    "modify_by"               varchar(36) COLLATE "pg_catalog"."default"  DEFAULT '':: character varying,
    "resource_ids"            varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "creator"                 varchar(255) COLLATE "pg_catalog"."default",
    "updater"                 varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT
ON COLUMN "public"."t_ds_process_definition"."name" IS '工作流名称';
COMMENT
ON COLUMN "public"."t_ds_process_definition"."version" IS '工作流版本';
COMMENT
ON COLUMN "public"."t_ds_process_definition"."release_state" IS '最新状态';
COMMENT
ON COLUMN "public"."t_ds_process_definition"."project_id" IS '所属项目id';
COMMENT
ON COLUMN "public"."t_ds_process_definition"."user_id" IS '用户id';
COMMENT
ON COLUMN "public"."t_ds_process_definition"."process_definition_json" IS '定义json';