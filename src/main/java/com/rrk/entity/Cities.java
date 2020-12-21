package com.rrk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 行政区域地州市信息表
 * </p>
 *
 * @author dinghao
 * @since 2020-12-08
 */
public class Cities extends Model<Cities> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String cityid;

    private String city;

    private String provinceid;


    public Integer getId() {
        return id;
    }

    public Cities setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getCityid() {
        return cityid;
    }

    public Cities setCityid(String cityid) {
        this.cityid = cityid;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Cities setCity(String city) {
        this.city = city;
        return this;
    }

    public String getProvinceid() {
        return provinceid;
    }

    public Cities setProvinceid(String provinceid) {
        this.provinceid = provinceid;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Cities{" +
        "id=" + id +
        ", cityid=" + cityid +
        ", city=" + city +
        ", provinceid=" + provinceid +
        "}";
    }
}
