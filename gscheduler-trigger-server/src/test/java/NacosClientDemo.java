import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.Properties;

/**
 * @Author seven
 * @Date 2021/5/20 10:36
 * @Description
 * @Version 1.0
 */
public class NacosClientDemo {
    public static void main(String[] args) throws NacosException {
        String serverAddr = "10.201.81.185:8840";
        String dataId = "kipf-dev";
        String group = "DEFAULT_GROUP";
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, dataId);
//        NamingService namingService = NacosFactory.createNamingService(properties);
//        System.out.println(namingService.getAllInstances("service-auth"));
    }
}
