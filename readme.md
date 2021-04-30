#   

## 部署增加文件说明

1. 添加gscheduler-api服务

```yaml

spring:
  cloud:
    gateway:
      routes:
        - id: gscheduler-api
          uri: lb://gscheduler-api
          predicates:
            - Path=/api/kipf/service/gscheduler/**
          filters:
            - StripPrefix=3
```

2. 数据表增加

```sql


-----------------------------------------------------
-- yijialuo 2021年4月28日 10:23:38                 --
-- gscheduler 调度触发器建模                        --
-----------------------------------------------------
DROP TABLE IF EXISTS "developer_gs"."gscheduler_trigger";
DROP SEQUENCE IF EXISTS "developer_gs"."g_scheduler_all_seq";
CREATE SEQUENCE "developer_gs"."g_scheduler_all_seq"
    INCREMENT 1
MINVALUE  1
MAXVALUE 999999999
START 1
CACHE 1
CYCLE;
-- ----------------------------
-- Table structure for gscheduler_trigger
-- ----------------------------
CREATE TABLE "developer_gs"."gscheduler_trigger"
(
    "id"          int4 NOT NULL DEFAULT nextval('"developer_gs".g_scheduler_all_seq'::regclass),
    "task_id"     varchar COLLATE "pg_catalog"."default",
    "corn"        varchar(255) COLLATE "pg_catalog"."default",
    "create_time" date,
    "update_time" date,
    "start_time"  date,
    "end_time"    date,
    "params"      varchar(255) COLLATE "pg_catalog"."default",
    "type"        varchar(255) COLLATE "pg_catalog"."default",
    "group_name"  varchar(255) COLLATE "pg_catalog"."default"
)
;
-- ----------------------------
-- Uniques structure for table gscheduler_trigger
-- ----------------------------
ALTER TABLE "developer_gs"."gscheduler_trigger"
    ADD CONSTRAINT "gscheduler_trigger_task_id_group_name_key" UNIQUE ("task_id", "group_name");

-- ----------------------------
-- Primary Key structure for table gscheduler_trigger
-- ----------------------------
ALTER TABLE "developer_gs"."gscheduler_trigger"
    ADD CONSTRAINT "gscheduler_trigger_pkey" PRIMARY KEY ("id");


-- ----------------------------
-- Table structure for t_ds_process_definition
-- ----------------------------
DROP TABLE IF EXISTS "developer_gs"."t_ds_process_definition";
CREATE TABLE "developer_gs"."t_ds_process_definition"
(
    "id"                      int4 NOT NULL                               DEFAULT nextval('"developer_gs".g_scheduler_all_seq'::regclass),
    "name"                    varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "version"                 int4,
    "release_state"           int4,
    "project_id"              int4,
    "user_id"                 varchar(255) COLLATE "pg_catalog"."default",
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
    "tenant_id"               int4                                        DEFAULT '-1':: integer,
    "update_time"             timestamp(6),
    "modify_by"               varchar(36) COLLATE "pg_catalog"."default"  DEFAULT '':: character varying,
    "resource_ids"            varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "creator"                 varchar(255) COLLATE "pg_catalog"."default",
    "updater"                 varchar(255) COLLATE "pg_catalog"."default"
)
;


-- ----------------------------
-- Uniques structure for table t_ds_process_definition
-- ----------------------------
ALTER TABLE "developer_gs"."t_ds_process_definition"
    ADD CONSTRAINT "process_definition_unique" UNIQUE ("name", "project_id");

-- ----------------------------
-- Primary Key structure for table t_ds_process_definition
-- ----------------------------
ALTER TABLE "developer_gs"."t_ds_process_definition"
    ADD CONSTRAINT "t_ds_process_definition_pkey" PRIMARY KEY ("id");


-- ----------------------------
-- Table structure for t_ds_project
-- ----------------------------
DROP TABLE IF EXISTS "developer_gs"."t_ds_project";
CREATE TABLE "developer_gs"."t_ds_project"
(
    "id"          int4 NOT NULL                               DEFAULT nextval('"developer_gs".g_scheduler_all_seq'::regclass),
    "name"        varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "description" varchar(200) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "user_id"     varchar(255) COLLATE "pg_catalog"."default",
    "flag"        int4                                        DEFAULT 1,
    "create_time" timestamp(6)                                DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp(6)                                DEFAULT CURRENT_TIMESTAMP,
    "creator"     varchar(255) COLLATE "pg_catalog"."default",
    "updater"     varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Uniques structure for table t_ds_project
-- ----------------------------
ALTER TABLE "developer_gs"."t_ds_project"
    ADD CONSTRAINT "t_ds_project_name_key" UNIQUE ("name");

-- ----------------------------
-- Primary Key structure for table t_ds_project
-- ----------------------------
ALTER TABLE "developer_gs"."t_ds_project"
    ADD CONSTRAINT "t_ds_project_pkey" PRIMARY KEY ("id");


-- t_ds_process_instance
DROP TABLE IF EXISTS "developer_gs"."t_ds_process_instance";
create table "developer_gs"."t_ds_process_instance"
(
    id                        integer      default nextval('"developer_gs".g_scheduler_all_seq'::regclass) not null
        constraint t_ds_process_instance_pkey
            primary key,
    name                      varchar(255) default NULL:: character varying,
    process_definition_id     integer,
    state                     integer,
    recovery                  integer,
    start_time                timestamp(6),
    end_time                  timestamp(6),
    run_times                 integer,
    host                      varchar(45)  default NULL:: character varying,
    command_type              integer,
    command_param             text,
    task_depend_type          integer,
    max_try_times             integer      default 0,
    failure_strategy          integer      default 0,
    warning_type              integer      default 0,
    warning_group_id          integer,
    schedule_time             timestamp(6),
    command_start_time        timestamp(6),
    global_params             text,
    process_instance_json     text,
    flag                      integer      default 1,
    update_time               timestamp(6),
    is_sub_process            integer      default 0,
    executor_id               integer                                                                      not null,
    locations                 text,
    connects                  text,
    history_cmd               text,
    dependence_schedule_times text,
    process_instance_priority integer,
    worker_group              varchar(64)  default '-1':: integer,
    timeout                   integer      default 0,
    tenant_id                 integer      default '-1':: integer not null
);


create
index process_instance_index
    on t_ds_process_instance (process_definition_id, id);

create
index start_time_index
    on t_ds_process_instance (start_time);
    
    
    
```

3. nacos 中yaml

gscheduler-api.yaml

```yaml
logging:
  level:
    com.gs.cd.gscheduler.quartz.job: debug
server:
  port: 18077
```