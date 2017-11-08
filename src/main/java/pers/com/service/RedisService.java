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

    private int WAIT_QUEUE = 300;
    private volatile boolean isEnd = false;
    private volatile AtomicInteger size = new AtomicInteger();

    @Autowired
    private PacketService packetService;

    @Autowired
    private RedisDao redisDao;

    private BlockingQueue<String> requestQueue = new ArrayBlockingQueue(WAIT_QUEUE);

    public boolean joinReuqestQueue(String tel) {
        if(size.get() < WAIT_QUEUE) {
            try {
                requestQueue.put(tel);
                size.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }else{
            System.out.println("网络堵塞");
        }
        return false;
    }

    public void start(String packetName){
        System.out.println("活动开始！");
        isEnd = false;
        while(!isEnd){
            if(!requestQueue.isEmpty()) {
                getRedPacket(packetName, requestQueue.poll());
            }
        }
    }

    public void stop(String packetName){
        System.out.println("活动结束！");
        isEnd = true;
        redisDao.delete(CommonConstant.RedisKey.SUCCESS_LIST);
        redisDao.delete(packetName);
        size.set(0);
        requestQueue.clear();
        packetService.deleteAll();
    }

    public void getRedPacket(String packetName, String tel){
        if(redisDao.getPacketsList().size(packetName) == 0){
            System.out.println("活动已经结束了");
            isEnd = true;
            return;
        }
        if(!redisDao.isMemberOfSuccessList(tel)) {
            String packetId = redisDao.getPacketsList().leftPop(packetName).toString();
            Packet packet = packetService.bindRedPacket(packetId, tel);
            if (null != packet) {
                System.out.println(tel+"抢到红包"+packet.getValue()+"元！");
                redisDao.addToSuccessList(tel);
            } else {
                System.out.println(tel+"抢红包出现了异常，现在恢复");
                redisDao.getPacketsList().leftPush(packetName, packetId);
            }
        }else{
            System.out.println(tel+"已经抢成功过一次！");
        }
        System.out.println("还剩"+redisDao.getPacketsList().size(packetName)+"个红包");
        System.out.println("已有"+redisDao.getSizeOfSuccessList()+"个人抢到");
    }

    public String checkRedPacket(String packetName, String tel){
        String resultMsg = "";
        if(redisDao.isMemberOfSuccessList(tel)){
            Packet packet = packetService.findByTel(tel);
            resultMsg += "恭喜您抢到一份金额为"+packet.getValue()+"元的红包";
        }else if(redisDao.getPacketsList().size(packetName) == 0){
            resultMsg += "您已经抢成功过一次";
            return resultMsg;
        }
        return resultMsg;
    }

}
