package com.gs.cd.gscheduler.server;


/**
 * @Author seven
 * @Date 2021/5/27 16:23
 * @Description
 * @Version 1.0
 */
public class Constant {
    //工作流定义
    public static class ProcessDefinitionPerms {
        private static final String name = "taskScheduling:workflowDefinition:";
        public static final String view = name + "view";
        public static final String edit = name + "edit";
        public static final String delete = name + "delete";
        public static final String add = name + "add";
        public static final String copy = name + "copy";
        public static final String export = name + "export";
        public static final String run_now = name + "runNow";
        public static final String timed_run = name + "timedRun";
    }

    //工作流实例
    public static class ProcessInstancePerms {
        private static final String name = "taskScheduling:workflowExample:";
        public static final String view = name + "view";
        public static final String edit = name + "edit";
        public static final String delete = name + "delete";
        public static final String rerun = name + "rerun";
        public static final String recovery_failed = name + "recoveryFailed";
        public static final String stop = name + "stop";
    }
}
