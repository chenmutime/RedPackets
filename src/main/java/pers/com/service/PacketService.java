package pers.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.com.dao.PacketDao;
import pers.com.dao.RedisDao;
import pers.com.model.Packet;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Service
@EnableTransactionManagement
public class PacketService {

    @Autowired
    private PacketDao packetDao;
    @Autowired
    private RedisDao redisDao;

    public int bindRedPacket(String id, String tel){
//        Packet packet = null;
//        if(null == packetDao.findByTel(tel)) {
//            packet = packetDao.findOne(id);
//            packet.setTel(tel);
//            packet = packetDao.save(packet);
//        }
        return packetDao.bindRedPacket(id, tel);
    }

    public void saveSmallPackets(String packetName, List<Packet> packets){
        packets = packetDao.save(packets);
        if(null != packets && !packets.isEmpty()){
            List<String> packetIds = packets.stream().map(Packet::getId).collect(Collectors.toList());
            redisDao.getPacketsList().leftPushAll(packetName,packetIds);
        }
    }

    public Packet findByTel(String tel){
        return packetDao.findByTel(tel);
    }

    public void deleteAll(){
        packetDao.deleteAll();
    }

}
