package pers.com.service;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;
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

    public static final int GOOD_SIZE = 1000;
    private int WAIT_QUEUE_SIZE = GOOD_SIZE*2;
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
//            System.out.println("系统繁忙");
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
//            if(size.get() == WAIT_QUEUE_SIZE && requestQueue.isEmpty() && !redisDao.isFinish(packetName)){
//                System.out.println("等待队列耗尽但是仍有库存，重新开放队列");
//                size.set(0);
//            }
        }
//        if(isFinish){
//            stop(packetName);
//        }
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
            if(StringUtils.isEmpty(packetId)){
                redisDao.addToFailedList(tel);
            }else {
                int result = packetService.bindRedPacket(packetId, tel);
                if (result > 0) {
                    System.out.println(tel + "抢到红包！");
                    redisDao.addToSuccessList(tel);
                } else {
                    System.out.println(tel + "抢红包出现了异常，现在恢复");
                    redisDao.addToFailedList(tel);
                    redisDao.getPacketsList().leftPush(packetName, packetId);
                }
            }
        }else{
//            System.out.println(tel+"已经抢成功过一次！");
        }
//        System.out.println("还剩"+redisDao.getPacketsList().size(packetName)+"个红包");
//        System.out.println("已有"+redisDao.getSizeOfSuccessList()+"个人抢到");
    }

    public Response checkRedPacket(String tel){
        Response response = new Response();
        String resultMsg = "";
        if(redisDao.isMemberOfSuccessList(tel)){
            Packet packet = packetService.findByTel(tel);
            resultMsg += "恭喜您抢到一份金额为"+packet.getValue()+"元的红包，现已存入您的账户";
            response.setStatus(2000);
            response.setMessage(resultMsg);
        }else if(redisDao.isMemberOfFailedList(tel)){
            resultMsg += "很遗憾失败了";
            response.setStatus(2000);
            response.setMessage(resultMsg);
        }else{
            response.setStatus(2001);
        }
        return response;
    }

}
