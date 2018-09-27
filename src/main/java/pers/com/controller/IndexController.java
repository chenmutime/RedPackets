package pers.com.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.com.service.PacketService;
import pers.com.service.RedisService;

import javax.annotation.Resource;
import java.util.Random;

/**
 * Created by chenmutime on 2017/11/7.
 */
@RestController
public class IndexController {

    @Resource
    private RedisService redisService;
    @Resource
    private PacketService packetService;


    //        参与秒杀
    @GetMapping("/miaosha")
    public String requestRedPacket() {
        if (redisService.joinRequestQueue("" + new Random().nextInt(20000), "red")) {
            return "success";
        }
        return "failed";
    }

    /**
     * 往数据库添加测试数据（红包）
     * PS：TaskService中已经实现了定时开启
     *
     * @param packetName
     * @return
     */
    @GetMapping("/start")
    @Async
    public String start(@RequestParam("packetName") String packetName) {
        packetService.start(packetName);
        return "success";
    }

    /**
     * 停止抢红包活动，并清除所有数据
     * PS：TaskService中已经实现了定时关闭
     *
     * @return
     */
    @GetMapping("/stop")
    public String stop(@RequestParam("packetName") String packetName) {
        redisService.stop(packetName);
        return "success";
    }
}

