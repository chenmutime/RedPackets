package pers.com.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.com.service.PacketService;
import pers.com.service.RedisService;

import javax.annotation.Resource;

/**
 * Created by minming.he on 2017/11/7.
 */
@RestController
public class IndexController {

    @Resource
    private RedisService redisService;
    @Resource
    private PacketService packetService;

    @RequestMapping("/miaosha")
    public String requestRedPacket(@RequestParam("tel")String tel){
        redisService.joinReuqestQueue(tel);
        return "success";
    }

    @RequestMapping("/start")
    @Async
    public String start(@RequestParam("packetName")String packetName){
//        List<Packet> list = new ArrayList<>(1000);
//        for(int i=0;i<1000;i++){
//            Packet packet = new Packet();
//            packet.setId(UUID.randomUUID().toString());
//            packet.setName(packetName);
//            packet.setValue(new Random().nextDouble());
//            list.add(packet);
//        }
//        packetService.saveSmallPackets(packetName, list);
        redisService.start(packetName);
        return "success";
    }

    @RequestMapping("/stop")
    @Async
    public String stop(){
        redisService.stop();
        return "success";
    }

    @RequestMapping("/check")
    public String check(@RequestParam("packetName")String packetName, @RequestParam("tel")String tel){
        return redisService.checkRedPacket(packetName, tel);
    }
}

