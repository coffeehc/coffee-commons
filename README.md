#coffee-commons项目
这个工程主要是对guice的的封装以及集成,在互联网的开发中经常需要用到如redis,mongog,zookeeper等服务器,两外还会用到web服务,这里主要是将这些服务进行了统一的集成,方便工程的调用,详细的介绍如下:

### coffee-common-base
> 这个包类似utils,主要是最简单的utils方法的集合,我不建议自己实现utils类,毕竟apache-commons和guava都已经帮我们实现了很多,主要是脚手架内部需要使用的方法封装,如类扫描等
> 这里面重点关注`com.coffee.common.base.AMainModule`这个抽象类,其实很简单,所有的模块的加载都在这里,详情请参考[README.md](https://github.com/coffeehc/coffee-commons/coffee-common-base/README.md)

### coffee-common-database
> 这个包主要是将阿里的[druid](https://github.com/alibaba/druid/)与guice进行了整合,提供统一的数据源服务
> 详情请参考[README.md](https://github.com/coffeehc/coffee-commons/coffee-common-database/README.md)

### coffee-common-redis
> 这是一个空项目,并没有对redis进行封装,除非特殊情况我会增加一些封装进来,这个包的主要作用就是提供redis的依赖

### coffee-common-zookeeper
> 同coffee-common-redis

### coffee-common-web
> 这里就比较厉害了,主要是封装了guice的一个mvc框架,比较简单也比较高效,剔除了我们不需要的一些J2EE功能,专注与url的路由以及对请求的封装,详情请参考[README.md](https://github.com/coffeehc/coffee-commons/coffee-common-web/README.md)

### coffee-common-jetty
> 你有没有想过配置tomcat好复杂,我发布一个web程序需要先安装web容器,不管是tomcat还是jetty还是weblogic等等,好烦啊,而且我也用不到容器里面的大部分功能,我要的只是能接收http请求,并封装好request和response传给我的filter或者servlet里面给我处理就行了,ok,这玩意就能提供这功能,将jetty内嵌到程序中,你只需要把这个模块集成到coffee-common-base里面,ok,直接命令行启动就好了

##TODO
还有很多文档没补充呢,慢慢来吧,如果你想忙帮,也可以联系我



2015年4月13日
coffeehc@gmail.com
