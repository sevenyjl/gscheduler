import cn.hutool.core.util.StrUtil;
import com.github.davidfantasy.mybatisplus.generatorui.GeneratorConfig;
import com.github.davidfantasy.mybatisplus.generatorui.MybatisPlusToolsApplication;
import com.github.davidfantasy.mybatisplus.generatorui.mbp.NameConverter;

public class GeberatorUIServer {
    static String db_ip="localhost";
    static String db_name="gscheduler";
    static String db_username="postgres";
    static String db_passwd="root";
    static String db_schema="public";
    static String basePackage="com.gs.cd.gscheduler.dao";

    public static void main(String[] args) {
        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:postgresql://"+db_ip+":5432/"+db_name+"?currentSchema=public&useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8")
                .userName(db_username)
                .password(db_passwd)
                .driverClassName("org.postgresql.Driver")
                //数据库schema，POSTGRE_SQL,ORACLE,DB2类型的数据库需要指定
                .schemaName(db_schema)
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
                .basePackage(basePackage)
                .port(8897)
                .build();
        MybatisPlusToolsApplication.run(config);
    }

}