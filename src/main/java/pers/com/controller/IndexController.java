package pers.com.controller;

import org.apache.coyote.Response;
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
 * Created by chenmutime on 2017/11/7.
 */
@RestController
public class IndexController {

    @Resource
    private RedisService redisService;

//    参与秒杀
    @RequestMapping("/miaosha")
    public String requestRedPacket(){
        if(redisService.joinReuqestQueue(""+System.currentTimeMillis())){
            return "success";
        }
        return "failed";
    }


    //前端定时请求查看是否抢到红包
    @RequestMapping("/check")
    public String check(@RequestParam("tel")String tel){
         return redisService.checkRedPacket(tel).getMessage();
    }
}

