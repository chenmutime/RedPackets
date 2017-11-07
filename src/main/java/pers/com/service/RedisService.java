package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.com.constant.CommonConstant;
import pers.com.model.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by minming.he on 2017/11/7.
 */
@Service
@EnableTransactionManagement
public class RedisService {

    private volatile boolean isEnd = false;

    @Autowired
    private PacketService packetService;

    @Autowired
    private RedisTemplate redisTemplate;

    private BlockingQueue<Integer> requestQueue = new ArrayBlockingQueue<Integer>(1000);

    public boolean joinReuqestQueue(Integer tel) {
        if(requestQueue.size() < 1000) {
            try {
                requestQueue.put(tel);
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
                getRedPacket(packetName, requestQueue.peek());
        }
    }

    public void stop(){
        System.out.println("活动结束！");
        isEnd = true;
    }

    public void getRedPacket(String packetName, Integer tel){
        if(redisTemplate.opsForList().size(packetName) == 0){
            return;
        }
        if(!redisTemplate.opsForSet().isMember(CommonConstant.RedisKey.SUCCESS_LIST,tel)) {
            String packetId = redisTemplate.opsForList().leftPop(packetName).toString();
            Packet packet = packetService.bindRedPacket(packetId, tel);
            if (null != packet) {
                System.out.println(tel+"抢到红包"+packet.getValue()+"元！");
                redisTemplate.opsForSet().add(CommonConstant.RedisKey.SUCCESS_LIST, tel);
            } else {
                redisTemplate.opsForList().leftPush(packetName, packetId);
            }
        }
    }

    public String checkRedPacket(String packetName, Integer tel){
        String resultMsg = "";
        if(redisTemplate.opsForSet().isMember(CommonConstant.RedisKey.SUCCESS_LIST,tel)){
            Packet packet = packetService.findByTel(tel);
            resultMsg += "恭喜您抢到一份金额为"+packet.getValue()+"元的红包";
        }else if(redisTemplate.opsForList().size(packetName) == 0){
            resultMsg += "抢红包失败";
            return resultMsg;
        }
        return resultMsg;
    }
}
