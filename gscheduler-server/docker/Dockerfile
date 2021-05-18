# FROM 设置基础镜像
FROM openjdk:11
# 设置镜像作者
MAINTAINER yijialuo
#设置环境信息
ENV TZ Asia/Shanghai
ENV LANG C.UTF-8
# -------docker启动参数------
# nacos服务地址
ENV NACOS_SERVER nacos-server:8848
# 命名空间
ENV NACOS_NAMESPACE pubilc
# 组
ENV NACOS_GROUP DEFAULT_GROUP
# 服务名称
ENV SERVICE_NAME gscheduler-server
#JVM参数
ENV JAVA_OPTS=""
# 添加运行jar
COPY ./target/gscheduler-server-*.jar /root/
# 设置运行权限
RUN echo '#!/bin/bash\n\
          var=`find /root/ -name *.jar`\n\
          jarName=${var##*/}\n\
          echo "=================GDocker==================="\n\
          echo "==  JVM参数：" $JAVA_OPTS\n\
          echo "==  nacos服务地址(nacos service address)：" $NACOS_SERVER\n\
          echo "==  服务的命名空间(nacos namesapce)：" $NACOS_NAMESPACE\n\
          echo "==  服务的group(nacos group)：" $DEFAULT_GROUP\n\
          echo "==  服务名称(service name)：" $SERVICE_NAME\n\
          echo "==  java版本(java version)：" $JAVA_HOME\n\
          echo "==  服务信息：" $jarName\n\
          echo "==========================================="\n\
          echo "---开始部署(Start deployment)---"\n\
          echo "运行命令：java ${JAVA_OPTS} -jar -Dspring.application.name=$SERVICE_NAME -Dspring.cloud.nacos.server-addr=$NACOS_SERVER -Dspring.cloud.nacos.config.namespace=$NACOS_NAMESPACE -Dspring.cloud.nacos.config.group=$NACOS_GROUP -Dspring.cloud.nacos.discovery.namespace=$NACOS_NAMESPACE /root/$jarName"\n\
          java -jar  ${JAVA_OPTS} \\\n\
          -Dspring.application.name=$SERVICE_NAME  \\\n\
          -Dspring.cloud.nacos.server-addr=$NACOS_SERVER  \\\n\
          -Dspring.cloud.nacos.config.namespace=$NACOS_NAMESPACE \\\n\
          -Dspring.cloud.nacos.config.group=$NACOS_GROUP \\\n\
          -Dspring.cloud.nacos.discovery.namespace=$NACOS_NAMESPACE \\\n\
          /root/$jarName' > /root/run.sh & chmod 777 /root/run.sh /root/*.jar
# 设置容器的入口
ENTRYPOINT ["/root/run.sh"]
