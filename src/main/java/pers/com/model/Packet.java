package pers.com.model;

import org.aspectj.lang.annotation.control.CodeGenerationHint;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

/**
 * Created by minming.he on 2017/11/7.
 */
@Table
@Entity
public class Packet {

    @Id
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private Integer value;
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
