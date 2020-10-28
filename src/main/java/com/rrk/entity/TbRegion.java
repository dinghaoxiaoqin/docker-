package com.rrk.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author dinghao
 * @since 2020-09-01
 */
@TableName("tb_region")
public class TbRegion extends Model<TbRegion> {

private static final long serialVersionUID=1L;

    /**
     * 区域主键
     */
    private Integer id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 区域上级标识
     */
    private Integer pid;

    /**
     * 地名简称
     */
    private String sname;

    /**
     * 区域等级
     */
    private Integer level;

    /**
     * 区域编码
     */
    private String citycode;

    /**
     * 邮政编码
     */
    private String yzcode;

    /**
     * 组合名称
     */
    private String mername;

    @TableField("Lng")
    private Float Lng;

    @TableField("Lat")
    private Float Lat;

    private String pinyin;


    public Integer getId() {
        return id;
    }

    public TbRegion setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TbRegion setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getPid() {
        return pid;
    }

    public TbRegion setPid(Integer pid) {
        this.pid = pid;
        return this;
    }

    public String getSname() {
        return sname;
    }

    public TbRegion setSname(String sname) {
        this.sname = sname;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public TbRegion setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public String getCitycode() {
        return citycode;
    }

    public TbRegion setCitycode(String citycode) {
        this.citycode = citycode;
        return this;
    }

    public String getYzcode() {
        return yzcode;
    }

    public TbRegion setYzcode(String yzcode) {
        this.yzcode = yzcode;
        return this;
    }

    public String getMername() {
        return mername;
    }

    public TbRegion setMername(String mername) {
        this.mername = mername;
        return this;
    }

    public Float getLng() {
        return Lng;
    }

    public TbRegion setLng(Float Lng) {
        this.Lng = Lng;
        return this;
    }

    public Float getLat() {
        return Lat;
    }

    public TbRegion setLat(Float Lat) {
        this.Lat = Lat;
        return this;
    }

    public String getPinyin() {
        return pinyin;
    }

    public TbRegion setPinyin(String pinyin) {
        this.pinyin = pinyin;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "TbRegion{" +
        "id=" + id +
        ", name=" + name +
        ", pid=" + pid +
        ", sname=" + sname +
        ", level=" + level +
        ", citycode=" + citycode +
        ", yzcode=" + yzcode +
        ", mername=" + mername +
        ", Lng=" + Lng +
        ", Lat=" + Lat +
        ", pinyin=" + pinyin +
        "}";
    }
}
