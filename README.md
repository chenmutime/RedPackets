# RedPackets
springboot+redis+mysql基于【单机环境】实现抢红包功能（漏斗算法）
- 当前端点击抢红包按钮时，如果出现“系统繁忙”，表示抢红包失败，允许按钮再次被点击；如果出现“抢红包中”，则按钮不允许再次点击，前端会定时请求后端，查询是否抢到，对于明确了抢到或未抢到的结果的，按钮恢复正常，但已经抢成功过的再次去抢会一直失败。
- 后端维护了一个size大于库存数量的等待队列，为的是限流，使用线程池从队列中获取请求，再从redis库存队列中为其分配一个id，这个id就是存在mysql的红包的主键，最后拿这个id去更新mysql，将红包与用户的手机号绑定在一起（存储过程）。当然，有可能失败，失败的情况下会将刚才从redis库存队列中拿到的id重新装填回去。


![基本架构图](http://cmtimeoss.oss-cn-shanghai.aliyuncs.com/RedPacket.png)

##测试：</br>
1、可以手动请求IndexController里的start方法，开启活动；然后调用stop方法结束活动，并清除所有影响下次测试的数据
2、也可以定时开启活动和结束活动，需要在TaskService里设置cron</br>
当活动开启时，使用JMeter请求(http://localhost:8080/miaosha)接口，对应miaosha这个方法里，我随机生产了数字用以表示手机号

我模拟10000个人抢1000个红包，等待队列为库存数量的3倍</br>
另外，我将mysql的最大连接数设置为了1000，并使用springboot默认的数据库连接池</br>

![测试图](http://cmtimeoss.oss-cn-shanghai.aliyuncs.com/qianghongbao.png)

![测试图2](http://cmtimeoss.oss-cn-shanghai.aliyuncs.com/RedPacket2.png)

##注意：</br>
我在将springboot版本由1x升级到2x时出现了一个关于调用存储过程的问题，详细请见：https://my.oschina.net/u/2283509/blog/2208787

