//package com.gs.cd.gscheduler.config;
//
//import com.gs.cd.gscheduler.api.*;
//import com.gs.cd.gscheduler.utils.Result;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestForApplicationRunner implements ApplicationRunner {
//
//    @Autowired
//    LoginApi loginApi;
//    @Autowired
//    AccessTokenApi accessTokenApi;
//    @Autowired
//    UsersApi usersApi;
//    @Autowired
//    ProjectApi projectApi;
//    @Autowired
//    ProcessDefinitionApi processDefinitionApi;
//    @Autowired
//    ProcessInstanceApi processInstanceApi;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        String sessionId = "sessionId=c5744ce2-574a-4466-a615-394447556a9f";
////        Result token = accessTokenApi.createToken("sessionId=d5d00bce-7c1d-4abd-9386-012c8d0769c1",
////                1, "2021-04-13 23:59:59", "86398077845a4bd398b91e6e9596fa94");
////        System.out.println(usersApi.listUser(sessionId));
////        Result project = projectApi.createProject(sessionId, "自动创建", "自动创建");
////        System.out.println(project);
////        System.out.println(processDefinitionApi.queryProcessDefinitionListPaging(sessionId, "自动创建", 100, 100, null, null));
//        System.out.println(processInstanceApi.queryProcessInstanceList(sessionId, "自动创建",null,null,null,null,null,null,null,1,10));
//    }
//
//}