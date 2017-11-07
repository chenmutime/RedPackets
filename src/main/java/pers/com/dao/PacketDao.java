package pers.com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.com.model.Packet;

/**
 * Created by minming.he on 2017/11/7.
 */
@Repository
public interface PacketDao extends JpaRepository<Packet, String> {

    Packet findByTel(Integer tel);
}
