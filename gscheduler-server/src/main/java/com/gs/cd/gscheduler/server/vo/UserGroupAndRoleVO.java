package com.gs.cd.gscheduler.server.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserGroupAndRoleVO implements Serializable {

    private List<String> userGroupId;
    private String roleId;



}