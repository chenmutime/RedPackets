package pers.com.service;

import com.google.common.util.concurrent.RateLimiter;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Service
public class RedisService {

    private Logger logger = LoggerFactory.getLogger(RedisService.class);

    public static final int GOOD_SIZE = 1000;
    private volatile boolean isFinish = false;

    @Autowired
    private PacketDao packetDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisDao redisDao;

    //    每秒允许有1000次访问，QPS
    private RateLimiter rateLimiter = RateLimiter.create(1000);

    public boolean joinRequestQueue(String tel, String packetName) {
        if (!isFinish && rateLimiter.tryAcquire()) {
            if (!redisDao.isMemberOfSuccessList(tel)) {
                Object packetId = redisDao.getPacketsList().leftPop(packetName);
                if (StringUtils.isEmpty(packetId)) {
                    logger.info(tel + "没有抢到红包");
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
                        logger.error(tel + "抢红包出现了异常，现在恢复库存");
                        redisDao.getPacketsList().rightPush(packetName, packetId);
                    }
                }
            } else {
                logger.warn(tel + "已经抢成功过一次！");
            }
            return true;
        }
        return false;
    }

    public void stop(String packetName) {
        isFinish = true;
        logger.info("共有" + redisDao.getSizeOfSuccessList() + "人抢到红包");
        logger.info("共有" + orderDao.count() + "人实际抢到红包");
        logger.info("剩余红包数为：" + redisDao.getPacketsList().size(packetName));
        orderDao.deleteAll();
        redisDao.delete(packetName);
        redisDao.delete(CommonConstant.RedisKey.SUCCESS_LIST);
    }

    public void start(String packetName) {
        logger.info("redis红包总数为：" + redisDao.getPacketsList().size(packetName));
        isFinish = false;
    }
}
