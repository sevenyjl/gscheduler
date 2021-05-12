package com.gs.cd.trigger.utils;

import cn.hutool.core.date.DateUtil;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;


/**
 * cron utils
 */
public class CronUtils {
    private CronUtils() {
        throw new IllegalStateException("CronUtils class");
    }
  public static CronExpression parse2CronExpression(String cronExpression) throws ParseException {
    return new CronExpression(cronExpression);
  }

    private static final Logger logger = LoggerFactory.getLogger(CronUtils.class);

    /**
     * gets all scheduled times for a period of time based on not self dependency
     * @param startTime startTime
     * @param endTime endTime
     * @param cronExpression cronExpression
     * @return date list
     */
  public static List<Date> getFireDateList(Date startTime, Date endTime, CronExpression cronExpression) {
    List<Date> dateList = new ArrayList<>();

    while (true) {
      startTime = cronExpression.getNextValidTimeAfter(startTime);
      if (startTime.after(endTime)) {
        break;
      }
      dateList.add(startTime);
    }

    return dateList;
  }

    /**
     * gets expect scheduled times for a period of time based on self dependency
     *
     * @param startTime      startTime
     * @param endTime        endTime
     * @param cronExpression cronExpression
     * @param fireTimes      fireTimes
     * @return date list
     */
    public static List<Date> getSelfFireDateList(Date startTime, Date endTime, CronExpression cronExpression, int fireTimes) {
        List<Date> dateList = new ArrayList<>();
        while (fireTimes > 0) {
            startTime = cronExpression.getNextValidTimeAfter(startTime);
            if (startTime.after(endTime) || startTime.equals(endTime)) {
                break;
            }
            dateList.add(startTime);
            fireTimes--;
        }

        return dateList;
    }


    /**
     * gets all scheduled times for a period of time based on self dependency
     * @param startTime startTime
     * @param endTime endTime
     * @param cronExpression cronExpression
     * @return date list
     */
  public static List<Date> getSelfFireDateList(Date startTime, Date endTime, CronExpression cronExpression) {
    List<Date> dateList = new ArrayList<>();

    while (true) {
      startTime = cronExpression.getNextValidTimeAfter(startTime);
      if (startTime.after(endTime) || startTime.equals(endTime)) {
        break;
      }
      dateList.add(startTime);
    }

    return dateList;
  }

    /**
     * gets all scheduled times for a period of time based on self dependency
     *
     * @param startTime startTime
     * @param endTime   endTime
     * @param cron      cron
     * @return date list
     */
    public static List<Date> getSelfFireDateList(Date startTime, Date endTime, String cron) {
        CronExpression cronExpression = null;
        try {
            cronExpression = parse2CronExpression(cron);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
        return getSelfFireDateList(startTime, endTime, cronExpression);
    }

    /**
     * get expiration time
     *
     * @param startTime startTime
     * @param cycleEnum cycleEnum
     * @return date
     */
    public static Date getExpirationTime(Date startTime, CycleEnum cycleEnum) {
        Date maxExpirationTime = null;
        Date startTimeMax = null;
        try {
            startTimeMax = getEndTime(startTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            switch (cycleEnum) {
                case HOUR:
                    calendar.add(Calendar.HOUR, 1);
                    break;
                case DAY:
                    calendar.add(Calendar.DATE, 1);
                    break;
                case WEEK:
                    calendar.add(Calendar.DATE, 1);
                    break;
                case MONTH:
                    calendar.add(Calendar.DATE, 1);
                    break;
                default:
                    logger.error("Dependent process definition's  cycleEnum is {},not support!!", cycleEnum);
                    break;
            }
            maxExpirationTime = calendar.getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DateUtil.compare(startTimeMax, maxExpirationTime) > 0 ? maxExpirationTime : startTimeMax;
    }

    /**
     * get the end time of the day by value of date
     *
     * @param date
     * @return date
     */
    private static Date getEndTime(Date date) {
        Calendar end = new GregorianCalendar();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end.getTime();
    }

}