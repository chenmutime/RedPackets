package pers.com.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.com.model.Packet;
import pers.com.service.PacketService;
import pers.com.service.RedisService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by minming.he on 2017/11/7.
 */
@RestController
public class IndexController {

    @Resource
    private RedisService redisService;
    @Resource
    private PacketService packetService;

//    参与秒杀
    @RequestMapping("/miaosha")
    public String requestRedPacket(@RequestParam("tel")String tel){
        redisService.joinReuqestQueue(tel);
        return "success";
    }

    @RequestMapping("/start")
    @Async
    public String start(@RequestParam("packetName")String packetName){
        List<Packet> list = new ArrayList<>(50);
        for(int i=0;i<50;i++){
            Packet packet = new Packet();
            packet.setId(UUID.randomUUID().toString());
            packet.setName(packetName);
            packet.setValue(new Random().nextInt(50));
            list.add(packet);
        }
        packetService.saveSmallPackets(packetName, list);
        redisService.start(packetName);
        return "success";
    }

    @RequestMapping("/execute")
    @Async
    public String execute(@RequestParam("packetName")String packetName){
        for(int i=1000;i<1200;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    redisService.joinReuqestQueue(""+new Random().nextInt(1000));
                }
            }).start();;
        }
        return "success";
    }

    @RequestMapping("/stop")
    @Async
    public String stop(@RequestParam("packetName")String packetName){
        redisService.stop(packetName);
        return "success";
    }

    //前端定时请求查看是否抢到红包
    @RequestMapping("/check")
    public String check(@RequestParam("packetName")String packetName, @RequestParam("tel")String tel){
        return redisService.checkRedPacket(packetName, tel);
    }
}

