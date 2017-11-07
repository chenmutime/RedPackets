package pers.com.model;

import org.aspectj.lang.annotation.control.CodeGenerationHint;

import javax.persistence.*;

/**
 * Created by minming.he on 2017/11/7.
 */
@Table
@Entity
public class Packet {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.TABLE, generator="payablemoney_gen")
    private String id;
    @Column
    private String name;
    @Column
    private Double value;
    @Column
    private Integer tel;

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getTel() {
        return tel;
    }

    public void setTel(Integer tel) {
        this.tel = tel;
    }
}
