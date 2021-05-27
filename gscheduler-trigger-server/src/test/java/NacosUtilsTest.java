

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @Author seven
 * @Date 2021/5/20 11:05
 * @Description
 * @Version 1.0
 */
@Slf4j
public class NacosUtilsTest {

    public static void main(String[] args) throws NacosException {
        String serverAddr = "10.201.81.185:8840";
        String namespace = "kipf-dev";
        String group = "DEFAULT_GROUP";
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);
        SevenConfigService sevenConfigService = new SevenConfigService(properties);
//        System.out.println(sevenConfigService.searchConfig("*sql*", group));
        SevenNameService sevenNameService = new SevenNameService(properties);
        ListView<String> servicesOfServer = sevenNameService.getServicesOfServer(1, Integer.MAX_VALUE);
        System.out.println(servicesOfServer);
//        System.out.println(namingService.getAllInstances("service-auth"));
//        String config = configService.getConfig("kipf_pg_biz_schema.sql", group, 5000);
//        System.out.println(config);
    }
}

@Slf4j
class SevenNameService extends NacosNamingService {
    private Properties properties;

    public String getNacosAddress() {
        return properties.getProperty(PropertyKeyConst.SERVER_ADDR);
    }

    public String getNacosNameSpace() {
        return properties.getProperty(PropertyKeyConst.NAMESPACE);
    }

    public SevenNameService(Properties properties) {
        super(properties);
        this.properties = properties;
    }
}

@Slf4j
class SevenConfigService extends NacosConfigService {
    private Properties properties;

    public SevenConfigService(Properties properties) throws NacosException {
        super(properties);
        this.properties = properties;
    }

    public String getNacosAddress() {
        return properties.getProperty(PropertyKeyConst.SERVER_ADDR);
    }

    public String getNacosNameSpace() {
        return properties.getProperty(PropertyKeyConst.NAMESPACE);
    }

    /**
     * 模糊查询配置
     *
     * @return
     */
    public HashMap<String, String> searchConfig(String dataId, String group) {
        return searchConfig(dataId, group, "", "", 1, 9999, "blur");
    }

    public HashMap<String, String> searchConfig(String dataId, String group, String appName, String config_tags, int pageNo, int pageSize, String search) {
        //模糊查询配置
        HashMap<String, Object> params = new HashMap<>();
        params.put("dataId", dataId);
        params.put("group", group);
        params.put("appName", appName);
        params.put("config_tags", config_tags);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("search", search);
        params.put("tenant", getNacosNameSpace());
        log.debug("查询地址：{}/nacos/v1/cs/configs，参数:{}", getNacosAddress(), params);
        String s = HttpUtil.get(getNacosAddress() + "/nacos/v1/cs/configs", params);
        log.debug("获取配置:{}", s);
        JSONObject jsonObject = JSONUtil.parseObj(s);
        JSONArray pageItems = jsonObject.getJSONArray("pageItems");
        //处理结果
        HashMap<String, String> result = new HashMap<>();
        pageItems.forEach(item -> {
            JSONObject jb = (JSONObject) item;
            result.put(jb.getStr("dataId"), jb.getStr("content"));
        });
        return result;
    }
}