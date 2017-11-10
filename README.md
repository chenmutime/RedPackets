# RedPackets
springboot+redis+mysql实现抢红包功能
- 当前端点击抢红包按钮时，如果出现“系统繁忙”，表示抢红包失败，允许按钮再次被点击；如果出现“抢红包中”，则按钮不允许再次点击，前端会定时请求后端，查询是否抢到，对于明确了抢到或未抢到的结果的，按钮恢复正常，但已经抢成功过的再次去抢会一直失败。
- 后端维护了一个size大于库存数量的等待队列，为的是限流，依次从队列中获取请求，再从redis库存队列中为其分配一个id，这个id就是存在mysql的红包的主键，最后拿这个id去更新mysql，将红包与用户的手机号绑定在一起（存储过程）。当然，有可能失败，失败的情况下会将刚才从redis库存队列中拿到的id重新装填回去。
- 如果出现了意外，等待队列的请求耗尽了，但是库存还有若干，那么这时候我会恢复等待队列，允许有新的请求加入（删除了，每次都判断影响性能）

![基本架构图](http://cmtimeoss.oss-cn-shanghai.aliyuncs.com/RedPacket.png)

##测试：</br>
测试的时候先用(http://localhost:8080/start?packetName=red)创建一些数据，packetName表示红包的名称
然后使用JMeter开始指定数量的线程请求(http://localhost:8080/miaosha)，对应miaosha这个方法里，我随机生产了数字用以表示手机号

我模拟10000个人抢1000个红包，等待队列为库存数量的3倍</br>
另外，我将mysql的最大连接数设置为了1000，并使用druid作为数据库连接池</br>

![测试图](http://cmtimeoss.oss-cn-shanghai.aliyuncs.com/qianghongbao.png)
