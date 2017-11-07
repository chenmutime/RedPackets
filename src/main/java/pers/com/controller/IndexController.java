package pers.com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.com.model.Packet;
import pers.com.service.RedisService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by minming.he on 2017/11/7.
 */
@RestController
public class IndexController {

    @Resource
    private RedisService redisService;

    @RequestMapping("/miaosha")
    public String requestRedPacket(@RequestParam("tel")Integer tel){
        redisService.joinReuqestQueue(tel);
        return "success";
    }

    @RequestMapping("/start")
    public String start(@RequestParam("packetName")String packetName){
        List<Packet> list = new ArrayList<>(1000);
        for(int i=0;i<1000;i++){
            Packet packet = new Packet();
            packet.setName(packetName);
            packet.setValue(new Random().nextDouble());
            list.add(packet);
        }
        redisService.start(packetName);
        return "success";
    }

    @RequestMapping("/stop")
    public String stop(){
        redisService.stop();
        return "success";
    }

    @RequestMapping("/check")
    public String check(@RequestParam("packetName")String packetName, @RequestParam("tel")Integer tel){
        return redisService.checkRedPacket(packetName, tel);
    }
}

