package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pers.com.dao.PacketDao;
import pers.com.dao.RedisDao;
import pers.com.model.Packet;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Service
public class PacketService {

    @Autowired
    private PacketDao packetDao;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RedisService redisService;

    public List<Packet> initDataBase(String packetName) {
        List<Packet> packets = packetDao.findAll();
        if (packets.isEmpty()) {
            List<Packet> list = new ArrayList<>(RedisService.GOOD_SIZE);
            for (int i = 0; i < RedisService.GOOD_SIZE; i++) {
                Packet packet = new Packet();
                packet.setId(UUID.randomUUID().toString());
                packet.setName(packetName);
                packet.setValue(new Random().nextInt(100));
                list.add(packet);
            }
            packets = packetDao.saveAll(list);
        }
        return packets;
    }

    public String start(String packetName) {
        List<Packet> packets = initDataBase(packetName);
        List<String> packetIds = packets.stream().map(Packet::getId).collect(Collectors.toList());
        redisDao.getPacketsList().leftPushAll(packetName, packetIds);
        redisService.start(packetName);
        return "success";
    }

    public String stop(String packetName) {
        redisService.stop(packetName);
        return "success";
    }

}
