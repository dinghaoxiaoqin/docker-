package com.rrk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 省份信息表
 * </p>
 *
 * @author dinghao
 * @since 2020-12-08
 */
public class Provinces extends Model<Provinces> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String provinceid;

    private String province;


    public Integer getId() {
        return id;
    }

    public Provinces setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getProvinceid() {
        return provinceid;
    }

    public Provinces setProvinceid(String provinceid) {
        this.provinceid = provinceid;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public Provinces setProvince(String province) {
        this.province = province;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Provinces{" +
        "id=" + id +
        ", provinceid=" + provinceid +
        ", province=" + province +
        "}";
    }
}
