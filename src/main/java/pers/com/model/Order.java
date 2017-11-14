package pers.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by minming.he on 2017/11/14.
 */
@Table(name = "i_order")
@Entity
public class Order {

    @Id
    @Column
    private String tel;
    @Column(name = "packet_id")
    private String packetId;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
}
