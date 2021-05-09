package gshceduler;

import cn.hutool.core.util.StrUtil;
import com.github.davidfantasy.mybatisplus.generatorui.GeneratorConfig;
import com.github.davidfantasy.mybatisplus.generatorui.MybatisPlusToolsApplication;
import com.github.davidfantasy.mybatisplus.generatorui.mbp.NameConverter;

public class GeberatorUIServer {

    public static void main(String[] args) {
        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:postgresql://10.201.82.253:5432/kipf?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8")
                .userName("postgres")
                .password("1qaz@WSX")
                .driverClassName("org.postgresql.Driver")
                //数据库schema，POSTGRE_SQL,ORACLE,DB2类型的数据库需要指定
                .schemaName("gscheduler")
                //如果需要修改各类生成文件的默认命名规则，可自定义一个NameConverter实例，覆盖相应的名称转换方法：
                .nameConverter(new NameConverter() {
                    @Override
                    public String entityNameConvert(String tableName) {
                        return StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
                    }
                    /**
                     * 自定义Service类文件的名称规则
                     */
                    @Override
                    public String serviceNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Service";
                    }
                    /**
                     * 自定义Controller类文件的名称规则
                     */
                    @Override
                    public String controllerNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Controller";
                    }
                })
                .basePackage("com.gs.cd.gscheduler.server")
                .port(8897)
                .build();
        MybatisPlusToolsApplication.run(config);
    }

}