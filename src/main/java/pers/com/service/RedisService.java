package pers.com.service;

import org.apache.coyote.Response;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pers.com.constant.CommonConstant;
import pers.com.dao.PacketDao;
import pers.com.dao.RedisDao;
import pers.com.model.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Service
public class RedisService {

    public static final int GOOD_SIZE = 1000;
    private int WAIT_QUEUE_SIZE = GOOD_SIZE*2;
    private volatile AtomicInteger size = new AtomicInteger();
    private volatile boolean isFinish = false;
    private Executor executor = Executors.newFixedThreadPool(9);

    @Autowired
    private PacketDao packetDao;

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
        }
        return false;
    }

    public void start(String packetName){
        System.out.println("活动开始！");
        isFinish = false;
        while(!isFinish){
            if(!requestQueue.isEmpty()) {
                String tel = requestQueue.poll();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(!redisDao.isMemberOfSuccessList(tel)) {
                            Object packetId = redisDao.getPacketsList().leftPop(packetName);
                            if(StringUtils.isEmpty(packetId)){
                                redisDao.addToFailedList(tel);
                            }else {
                                try {
                                    int result = packetDao.bindRedPacket(packetId.toString(), tel);
                                    if (result > 0) {
                                        System.out.println(tel + "抢到红包！");
                                        redisDao.addToSuccessList(tel);
                                    } else {
                                        throw new Exception();
                                    }
                                }catch (Exception e){
//                                    如果重复插入手机号会出现主键重复异常，这时候恢复库存；由于前端的控制，理论上不会重复提交手机号，但为了防止意外或者恶意请求的发升，因此try catch
                                    System.out.println(tel + "抢红包出现了异常，现在恢复");
                                    redisDao.getPacketsList().rightPush(packetName, packetId);
                                }
                            }
                        }else{
                            System.out.println(tel+"已经抢成功过一次！");
                        }
                    }
                });
            }
        }
    }

    public void stop(String packetName){
        System.out.println("活动结束！已处理"+size.get()+"人");
        System.out.println("还剩"+redisDao.getPacketsList().size(packetName)+"个红包");
        System.out.println("已有"+redisDao.getSizeOfSuccessList()+"个人抢到");
        isFinish = true;
        redisDao.delete(CommonConstant.RedisKey.SUCCESS_LIST);
        redisDao.delete(CommonConstant.RedisKey.FAILED_LIST);
        redisDao.delete(packetName);
        size.set(0);
        requestQueue.clear();
        packetDao.deleteAll();
    }

    public Response checkRedPacket(String tel){
        Response response = new Response();
        String resultMsg = "";
        if(redisDao.isMemberOfSuccessList(tel)){
            Packet packet = packetDao.findByTel(tel);
            resultMsg += "恭喜您抢到一份金额为"+packet.getValue()+"元的红包，现已存入您的账户";
            response.setStatus(2000);
            response.setMessage(resultMsg);
        }else if(redisDao.isMemberOfFailedList(tel)){
            resultMsg += "很遗憾失败了";
            response.setStatus(2000);
            response.setMessage(resultMsg);
        }else{
            response.setStatus(2001);
            response.setMessage("继续等待");
        }
        return response;
    }

}
