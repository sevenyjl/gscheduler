分布式定时触发器服务

原来设计理念：

记录定时执行的服务地址，当请来时 去记录的服务地址停止定时任务。


修改1：分片思想


修改2：
（创建一个定时任务，redis来限定服务）服务1启动， 读取redis是否有master注册上了，
如果注册了，检测服务可用性（向nacos发送心跳）
    如果心跳成功，缓存master服务
    如果心跳失败，在nacos上获取服务，并注册上去。读取未创建的定时任务，并调用remoteAdd创建
如果没有注册，在nacos上获取服务，并注册上去。读取未创建的定时任务，并调用remoteAdd创建

每个服务提供 remoteAdd、remoteDel能力拥有删除master上的定时任务

接口提供，type http|nacos 的能力
nacos：能通过nacos来获取到服务部署地址从而走服务间的轮询调用（参考gateway）；
http：直接创建http服务号

模拟场景：
1.Service1、Service2、Service3启动，定时监听master是否注册，并选举Service1。Service1创建定时任务。
如果Service2检测到Service1挂了，Service2立刻获取新的服务，并随机选择一个服务作为master并注册到redis上