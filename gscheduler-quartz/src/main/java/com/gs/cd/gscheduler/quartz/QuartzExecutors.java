package com.gs.cd.gscheduler.quartz;


import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.gs.cd.gscheduler.quartz.job.GschedulerTriggerJob;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

@Component
@Slf4j
public class QuartzExecutors {
    Scheduler scheduler;

    public QuartzExecutors() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }

    public void addJob(String tenantCode, GschedulerTrigger gschedulerTrigger) {
        addJob(
                GschedulerTriggerJob.class, gschedulerTrigger.getTaskId(), tenantCode + "," + gschedulerTrigger.getGroupName(), gschedulerTrigger.getStartTime(), gschedulerTrigger.getEndTime(),
                gschedulerTrigger.getCorn(), Map.of("gschedulerTrigger", gschedulerTrigger));
    }

    private void addJob(Class<? extends Job> clazz, String jobName, String jobGroupName, Date startDate, Date endDate,
                        String cronExpression,
                        Map<String, Object> jobDataMap) {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroupName);
            JobDetail jobDetail;
            if (scheduler.checkExists(jobKey)) {

                jobDetail = scheduler.getJobDetail(jobKey);
                if (jobDataMap != null) {
                    jobDetail.getJobDataMap().putAll(jobDataMap);
                }
            } else {
                jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).build();

                if (jobDataMap != null) {
                    jobDetail.getJobDataMap().putAll(jobDataMap);
                }

                scheduler.addJob(jobDetail, false, true);
            }
            TriggerKey triggerKey = new TriggerKey(jobName, jobGroupName);
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).startAt(startDate).endAt(endDate)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing())
                    .forJob(jobDetail).build();

            if (scheduler.checkExists(triggerKey)) {
                // updateProcessInstance scheduler trigger when scheduler cycle changes
                CronTrigger oldCronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                String oldCronExpression = oldCronTrigger.getCronExpression();

                if (!StrUtil.equalsIgnoreCase(cronExpression, oldCronExpression)) {
                    // reschedule job trigger
                    scheduler.rescheduleJob(triggerKey, cronTrigger);
                }
            } else {
                scheduler.scheduleJob(cronTrigger);
            }
        } catch (Exception e) {
            throw new RuntimeException("add job failed", e);
        }
    }

    public boolean deleteJob(String tenantCode, String jobName, String jobGroupName) {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroupName);
            if (scheduler.checkExists(jobKey)) {
                log.info("删除定时job, job name: {}, job group name: {},", jobName, jobGroupName);
                return scheduler.deleteJob(jobKey);
            } else {
                return true;
            }

        } catch (SchedulerException e) {
            log.error("删除定时失败 job : {}", jobName, e);
        }
        return false;
    }

    public boolean deleteAllJobs(String tenantCode, String jobGroupName) {
        try {
            log.info("尝试删除JobGroupName中的所有job: {}", jobGroupName);
            List<JobKey> jobKeys = new ArrayList<>();
            jobKeys.addAll(scheduler.getJobKeys(GroupMatcher.groupEndsWith(jobGroupName)));

            return scheduler.deleteJobs(jobKeys);
        } catch (SchedulerException e) {
            log.error("尝试删除JobGroupName中的所有job: {} 失败了 {}", jobGroupName, e);
        }
        return false;
    }

}