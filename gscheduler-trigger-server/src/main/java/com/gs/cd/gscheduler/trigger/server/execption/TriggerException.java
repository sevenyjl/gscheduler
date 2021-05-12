package com.gs.cd.gscheduler.trigger.server.execption;

/**
 * @Author seven
 * @Date 2021/5/12 11:08
 * @Description
 * @Version 1.0
 */
public class TriggerException extends RuntimeException {
    public TriggerException() {
    }

    public TriggerException(String message) {
        super(message);
    }

    public TriggerException(String message, Throwable cause) {
        super(message, cause);
    }
}
