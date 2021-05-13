package com.gs.cd.gscheduler.trigger.server.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/5/13 9:45
 * @Description
 * @Version 1.0
 */
@Service
public class NacosService {
    @Autowired
    NacosConfigProperties nacosConfigProperties;
    @Autowired
    ServerProperties serverProperties;
    @Value("${spring.application.name}")
    private String serviceName;

    public boolean addressIsExist(String address) {
        List<NacosServiceBean> nacosServiceBeans = nacosServiceBeanList(nacosConfigProperties.getClusterName(), nacosConfigProperties.getNamespace());
        NacosServiceBean nacosServiceBean = nacosServiceBeans.stream()
                .filter(s -> s.isEnabled() && s.getAddress().equals(address))
                .findFirst().orElse(null);
        return nacosServiceBean != null;
    }

    private List<NacosServiceBean> nacosServiceBeanList(String clusterName, String namespaceId) {
        String serverAddr = nacosConfigProperties.getServerAddr();
        HashMap<String, Object> params = new HashMap<>();
        params.put("serviceName", serviceName);
        if (StrUtil.isNotEmpty(clusterName)) {
            params.put("clusterName", clusterName);
        }
        if (StrUtil.isNotEmpty(namespaceId)) {
            params.put("namespaceId", namespaceId);
        }
        params.put("healthyOnly", true);
        String jsonStr = HttpUtil.get(serverAddr + "/nacos/v1/ns/instance/list", params);
        JSONArray hosts = JSONUtil.parseObj(jsonStr).getJSONArray("hosts");
        return hosts.toList(NacosServiceBean.class);
    }

    public List<NacosServiceBean> listAll() {
        return nacosServiceBeanList(nacosConfigProperties.getClusterName(), nacosConfigProperties.getNamespace());
    }

    public String getFirstServerAddress() {
        List<NacosServiceBean> nacosServiceBeans = nacosServiceBeanList(nacosConfigProperties.getClusterName(), nacosConfigProperties.getNamespace());
        if (!nacosServiceBeans.isEmpty()) {
            for (NacosServiceBean nacosServiceBean : nacosServiceBeans) {
                if (nacosServiceBean.isEnabled()) {
                    return nacosServiceBean.getAddress();
                }
            }
        }
        return null;
    }

    @Data
    public static class NacosServiceBean {
        private String ip;
        private Integer port;
        private Float weight;
        private boolean healthy;
        private boolean enabled;
        private boolean ephemeral;
        private String clusterName;
        private String serviceName;

        public String getAddress() {
            return ip + ":" + port;
        }
    }

}
