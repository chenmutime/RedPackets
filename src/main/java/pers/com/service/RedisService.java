package pers.com.service;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pers.com.constant.CommonConstant;
import pers.com.dao.OrderDao;
import pers.com.dao.PacketDao;
import pers.com.dao.RedisDao;
import pers.com.model.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Service
public class RedisService {

    private Logger logger = LoggerFactory.getLogger(RedisService.class);

    public static final int GOOD_SIZE = 1000;
    private int WAIT_QUEUE_SIZE = GOOD_SIZE * 3;
    private AtomicInteger size = new AtomicInteger();
    private volatile boolean isFinish = false;
    private volatile int repeatCount = 0;
    private volatile int requestCount = 0;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private PacketDao packetDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisDao redisDao;

    private BlockingQueue<String> requestQueue = new ArrayBlockingQueue(WAIT_QUEUE_SIZE);

    public boolean joinRequestQueue(String tel) {
        if (size.getAndIncrement() <= WAIT_QUEUE_SIZE) {
            try {
                requestQueue.put(tel);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

    public void start(String packetName) {
        logger.info("活动开始");
        logger.info("缓存中红包个数为：" + String.valueOf(redisDao.getPacketsList().size(packetName)));
        isFinish = false;
        while (!isFinish) {
            if (!requestQueue.isEmpty()) {
                String tel = requestQueue.poll();
                threadPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        ++requestCount;
                        if (!redisDao.isMemberOfSuccessList(tel)) {
                            Object packetId = redisDao.getPacketsList().leftPop(packetName);
                            if (StringUtils.isEmpty(packetId)) {
//                                这里没有直接设置isFinish为true，是因为可能同时存在其他线程虽然从redis得到了packetId，
//                              但由于异常的原因，导致packetId归还到redis里去了，这样其他用户可以再次请求
                                logger.info(tel + "没有抢到红包");
                                redisDao.addToFailedList(tel);
                            } else {
                                try {
                                    int result = packetDao.bindRedPacket(packetId.toString(), tel);
                                    if (result >= 0) {
                                        logger.info(tel + "抢到红包！");
                                        redisDao.addToSuccessList(tel);
                                    } else {
                                        logger.info(tel + "已经抢到过红包");
                                        throw new Exception();
                                    }
                                } catch (Exception e) {
//                                 如果重复插入手机号会出现主键重复异常，这时候恢复库存；
//                                 由于前端的控制，理论上不会重复提交手机号，但为了防止意外或者恶意请求的发升，因此try catch
//                                对于拦截重复手机号，后端做了3层拦截
//                                第一层：redisDao.isMemberOfSuccessList；
//                                第二层：存储过程中根据手机号查询i_order表是否已经存在数据
//                                第三层：存储过程中，由于两个线程同时请求，有可能都会从i_order表中查不到数据，最终都会执行插入操作
//                                         但由于tel是不允许重复的，那么就依靠重复异常抛出错误了
                                    logger.error(tel + "抢红包出现了异常，现在恢复库存");
                                    ++repeatCount;
                                    redisDao.getPacketsList().rightPush(packetName, packetId);
                                }
                            }
                        } else {
                            ++repeatCount;
                            logger.warn(tel + "已经抢成功过一次！");
                        }
                    }
                });
            }
        }
    }

    public void stop(String packetName) {
        System.out.println("还剩" + redisDao.getPacketsList().size(packetName) + "个红包");
        System.out.println("已有" + redisDao.getSizeOfSuccessList() + "个人抢到");
        System.out.println("有" + repeatCount + "次重复请求");
        System.out.println("有" + requestCount + "次请求参与抢红包");
        System.out.println("还剩" + requestQueue.size() + "个请求未被处理");
        System.out.println("有" + redisDao.getSizeOfFailedList() + "个请求抢购失败了");
        System.out.println("请求队列中共有" + size.get() + "名用户");
        isFinish = true;
        redisDao.delete(CommonConstant.RedisKey.SUCCESS_LIST);
        redisDao.delete(CommonConstant.RedisKey.FAILED_LIST);
        redisDao.delete(packetName);
        size.set(0);
        repeatCount = 0;
        requestCount = 0;
        requestQueue.clear();
        orderDao.deleteAll();
    }

    public Response checkRedPacket(String tel) {
        Response response = new Response();
        String resultMsg = "";
        if (redisDao.isMemberOfSuccessList(tel)) {
            Packet packet = packetDao.findByTel(tel);
            resultMsg += "恭喜您抢到一份金额为" + packet.getValue() + "元的红包，现已存入您的账户";
            response.setStatus(2000);
            response.setMessage(resultMsg);
        } else if (redisDao.isMemberOfFailedList(tel)) {
            resultMsg += "很遗憾失败了";
            response.setStatus(2000);
            response.setMessage(resultMsg);
        } else {
            response.setStatus(2001);
            response.setMessage("继续等待");
        }
        return response;
    }


}
