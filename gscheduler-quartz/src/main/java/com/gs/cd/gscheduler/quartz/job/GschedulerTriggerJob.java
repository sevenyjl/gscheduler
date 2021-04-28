package com.gs.cd.gscheduler.quartz.job;

import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.common.entity.ITrigger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
@Data
@Slf4j
public class GschedulerTriggerJob implements Serializable, Job {


    public GschedulerTriggerJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobKey key = jobDetail.getKey();
        log.debug("定时任务name={},group={},des={}", key.getName(), key.getGroup(), jobDetail.getDescription());
        GschedulerTrigger gschedulerTrigger = (GschedulerTrigger) jobExecutionContext.getMergedJobDataMap().get("gschedulerTrigger");
        log.debug("转换gschedulerTrigger=" + gschedulerTrigger);
        ITrigger iTrigger = GschedulerTrigger.params2ITrigger(gschedulerTrigger);
        try {
            iTrigger.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
