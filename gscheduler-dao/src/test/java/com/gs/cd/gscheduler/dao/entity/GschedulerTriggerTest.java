package com.gs.cd.gscheduler.dao.entity;

import org.junit.Test;

/**
 * @Author seven
 * @Date 2021/4/27 14:49
 * @Description
 * @Version 1.0
 */
public class GschedulerTriggerTest {
    @Test
    public void demo01() {
        System.out.println(findRepeatNumber(new int[]{2, 3, 1, 0, 2, 5, 3}));
        System.out.println(findRepeatNumber(new int[]{0, 1, 2, 3, 4, 11, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}));
    }

    public static int findRepeatNumber(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int i1 = i+1; i1 < nums.length; i1++) {
                if (nums[i] == nums[i1]) {
                    return nums[i];
                }
            }
        }
        return -1;
    }

}