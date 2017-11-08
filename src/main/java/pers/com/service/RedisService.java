package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.com.constant.CommonConstant;
import pers.com.dao.RedisDao;
import pers.com.model.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by minming.he on 2017/11/7.
 */
@Service
@EnableTransactionManagement
public class RedisService {

    private volatile boolean isEnd = false;
    private volatile AtomicInteger size = new AtomicInteger();

    @Autowired
    private PacketService packetService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisDao redisDao;

    private BlockingQueue<String> requestQueue = new ArrayBlockingQueue(1000);

    public boolean joinReuqestQueue(String tel) {
        if(size.get() < 1000) {
            try {
                requestQueue.put(tel);
                size.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void start(String packetName){
        System.out.println("活动开始！");
        while(!isEnd){
            if(!requestQueue.isEmpty())
                getRedPacket(packetName, requestQueue.poll());
        }
    }

    public void stop(){
        System.out.println("活动结束！");
        isEnd = true;
    }

    public void getRedPacket(String packetName, String tel){
        if(redisDao.getPacketsList().size(packetName) == 0){
            return;
        }
        if(!redisDao.getSuccessList().contains(tel)) {
            String packetId = redisDao.getPacketsList().leftPop(packetName).toString();
            Packet packet = packetService.bindRedPacket(packetId, tel);
            if (null != packet) {
                System.out.println(tel+"抢到红包"+packet.getValue()+"元！");
                redisDao.getSuccessList().add(tel);
            } else {
                redisDao.getPacketsList().leftPush(packetName, packetId);
            }
        }
    }

    public String checkRedPacket(String packetName, String tel){
        String resultMsg = "";
        if(redisDao.getSuccessList().contains(tel)){
            Packet packet = packetService.findByTel(tel);
            resultMsg += "恭喜您抢到一份金额为"+packet.getValue()+"元的红包";
        }else if(redisDao.getPacketsList().size(packetName) == 0){
            resultMsg += "抢红包失败";
            return resultMsg;
        }
        return resultMsg;
    }
}
