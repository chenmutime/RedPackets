package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.com.dao.PacketDao;
import pers.com.model.Packet;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by minming.he on 2017/11/7.
 */
@Service
@EnableTransactionManagement
public class PacketService {

    @Autowired
    private PacketDao packetDao;
    @Autowired
    private RedisTemplate redisTemplate;

    public Packet bindRedPacket(String id, Integer tel){
        Packet packet = null;
        if(null == packetDao.findByTel(tel)) {
            packet = packetDao.getOne(id);
            packet.setTel(tel);
            packet = packetDao.save(packet);
        }
        return packet;
    }

    public void saveSmallPackets(String packetName, List<Packet> packets){
        packets = packetDao.save(packets);
        if(null != packets && !packets.isEmpty()){
            List<String> packetIds = packets.stream().map(Packet::getId).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(packetName,packetIds);
        }
    }

    public Packet findByTel(Integer tel){
        return packetDao.findByTel(tel);
    }

}
