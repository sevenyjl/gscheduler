package com.gs.cd.gscheduler.trigger.server.job;

import com.gs.cd.gscheduler.trigger.server.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.trigger.server.entity.ITrigger;
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
        GschedulerTrigger gschedulerTrigger = (GschedulerTrigger) jobExecutionContext.getMergedJobDataMap().get("gschedulerTrigger");
        if (gschedulerTrigger.getSuspendFlag()) {
            log.debug("暂停定时任务了");
        } else {
            log.debug("定时任务name={},group={},des={}", key.getName(), key.getGroup(), jobDetail.getDescription());
            log.debug("转换gschedulerTrigger=" + gschedulerTrigger);
            ITrigger iTrigger = gschedulerTrigger.params2ITrigger();
            try {
                if (iTrigger == null) {
                    log.warn("触发器错误,params为空：{}", gschedulerTrigger);
                } else {
                    iTrigger.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
