package pers.com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pers.com.model.Packet;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Repository
public interface PacketDao extends JpaRepository<Packet, String> {

    Packet findByTel(String tel);

    @Procedure(procedureName = "bind", outputParameterName = "o_result")
    Integer bindRedPacket(@Param("i_id") String id, @Param("i_tel") String tel);

}