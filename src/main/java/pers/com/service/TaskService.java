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

    //模拟某日某时开启抢红包活动
    @Scheduled(cron = "0 40 14 * * ?")
    public void taskStart(){
        packetService.start("red");
    }

    @Scheduled(cron = "0 45 14 * * ?")
    public void taskStop(){
        packetService.stop("red");
    }
}
