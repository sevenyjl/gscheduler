package com.gs.cd.gscheduler.server.job;

import com.gs.cd.gscheduler.common.entity.ITrigger;
import com.gs.cd.gscheduler.dao.entity.GschedulerTrigger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
//        ITrigger iTrigger = gschedulerTrigger.params2ITrigger();
//        iTrigger.execute();
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
