package pers.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by chenmutime on 2017/11/7.
 */
@Table(name = "i_packet")
@Entity
public class Packet {

    @Id
    @Column
    private String id;
//    红包名称
    @Column
    private String name;
//    随机金额
    @Column
    private Integer value;
//    绑定的手机号
    @Column
    private String tel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
