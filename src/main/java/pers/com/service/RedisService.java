package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.com.constant.CommonConstant;
import pers.com.dao.RedisDao;
import pers.com.model.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Service
@EnableTransactionManagement
public class RedisService {

    public static final int GOOD_SIZE = 100;
    private int WAIT_QUEUE_SIZE = 120;
    private volatile boolean isFinish = false;
    private volatile AtomicInteger size = new AtomicInteger();

    @Autowired
    private PacketService packetService;

    @Autowired
    private RedisDao redisDao;

    private BlockingQueue<String> requestQueue = new ArrayBlockingQueue(WAIT_QUEUE_SIZE);

    public boolean joinReuqestQueue(String tel) {
        if(size.get() < WAIT_QUEUE_SIZE) {
            try {
                requestQueue.put(tel);
                size.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }else{
            System.out.println("系统繁忙");
        }
        return false;
    }

    public void start(String packetName){
        System.out.println("活动开始！");
        isFinish = false;
        while(!isFinish){
            if(!requestQueue.isEmpty()) {
                if(redisDao.isFinish(packetName)){
                    System.out.println("活动已经结束了");
                    isFinish = true;
                    break;
                }
                getRedPacket(packetName, requestQueue.poll());
            }
            if(size.get() == WAIT_QUEUE_SIZE && requestQueue.isEmpty() && !redisDao.isFinish(packetName)){
                System.out.println("等待队列耗尽但是仍有库存，重新开放队列");
                size.set(0);
            }
        }
        if(isFinish){
            stop(packetName);
        }
    }

    public void stop(String packetName){
        System.out.println("活动结束！");
        isFinish = true;
        redisDao.delete(CommonConstant.RedisKey.SUCCESS_LIST);
        redisDao.delete(packetName);
        size.set(0);
        requestQueue.clear();
        packetService.deleteAll();
    }

    public void getRedPacket(String packetName, String tel){
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
