package com.gs.cd.gscheduler.server.job;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gs.cd.gscheduler.common.enums.TriggerType;
import com.gs.cd.gscheduler.common.enums.entity.HttpParams;
import com.gs.cd.gscheduler.common.enums.entity.ITrigger;
import com.gs.cd.gscheduler.dao.entity.GschedulerTrigger;
import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
@Data
public class GschedulerTriggerJob implements Serializable, Job {


    public GschedulerTriggerJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        ITrigger iTrigger = gschedulerTrigger.params2ITrigger();
//        iTrigger.execute();
        System.out.println("job name=" + jobExecutionContext.getJobDetail().getKey() + "---" + new Date() + "->执行了->" + jobExecutionContext.getMergedJobDataMap().get("data").toString());
    }
}
