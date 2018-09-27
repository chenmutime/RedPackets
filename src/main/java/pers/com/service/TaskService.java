package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by chenmutime on 2017/11/10.
 */
@Component
public class TaskService {

    @Autowired
    private PacketService packetService;
    @Autowired
    private RedisService redisService;
//
//    //模拟某日某时开启抢红包活动
//    @Scheduled(cron = "0 12 16 * * ?")
//    public void taskStart(){
//        packetService.start("red");
//    }
//
////    5分钟后停止活动
//    @Scheduled(cron = "0 15 16 * * ?")
//    public void taskStop(){
//        redisService.stop();
//    }
}
