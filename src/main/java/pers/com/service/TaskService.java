package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by minming.he on 2017/11/10.
 */
@Component
public class TaskService {

    @Autowired
    private PacketService packetService;

    //模拟某日某时开启抢红包活动
    @Scheduled(cron = "0 13 11 * * ?")
    public void taskStart(){
        packetService.start("red");
    }

    @Scheduled(cron = "0 15 11 * * ?")
    public void taskStop(){
        packetService.stop("red");
    }
}
