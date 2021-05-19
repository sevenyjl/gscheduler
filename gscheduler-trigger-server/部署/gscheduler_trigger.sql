/*
 Navicat Premium Data Transfer

 Source Server         : 10.201.82.253
 Source Server Type    : PostgreSQL
 Source Server Version : 120002
 Source Host           : 10.201.82.253:5432
 Source Catalog        : kipf
 Source Schema         : gscheduler_trigger

 Target Server Type    : PostgreSQL
 Target Server Version : 120002
 File Encoding         : 65001

 Date: 12/05/2021 11:29:02

DROP SCHEMA IF EXISTS "gscheduler_trigger" CASCADE;
CREATE SCHEMA "gscheduler_trigger";
*/

DROP SCHEMA IF EXISTS "gscheduler_trigger" CASCADE;
CREATE SCHEMA "gscheduler_trigger";
-- ----------------------------
-- Table structure for gscheduler_trigger
-- ----------------------------
DROP TABLE IF EXISTS "gscheduler_trigger"."gscheduler_trigger";
CREATE TABLE "gscheduler_trigger"."gscheduler_trigger"
(
    "id"          serial8,
    "task_id"      varchar COLLATE "pg_catalog"."default",
    "corn"         varchar(255) COLLATE "pg_catalog"."default",
    "create_time"  date,
    "update_time"  date,
    "start_time"   date,
    "end_time"     date,
    "params"       text COLLATE "pg_catalog"."default",
    "type"         varchar(255) COLLATE "pg_catalog"."default",
    "group_name"   varchar(255) COLLATE "pg_catalog"."default",
    "lock_flag"    bool,
    "tenant_code"  varchar(255) COLLATE "pg_catalog"."default",
    "address"      varchar(255) COLLATE "pg_catalog"."default",
    "suspend_flag" bool
)
;
COMMENT
ON COLUMN "gscheduler_trigger"."gscheduler_trigger"."suspend_flag" IS '暂停标识';

-- ----------------------------
-- Uniques structure for table gscheduler_trigger
-- ----------------------------
ALTER TABLE "gscheduler_trigger"."gscheduler_trigger"
    ADD CONSTRAINT "gscheduler_trigger_task_id_group_name_key" UNIQUE ("task_id", "group_name");

-- ----------------------------
-- Primary Key structure for table gscheduler_trigger
-- ----------------------------
ALTER TABLE "gscheduler_trigger"."gscheduler_trigger"
    ADD CONSTRAINT "gscheduler_trigger_pkey" PRIMARY KEY ("id");
