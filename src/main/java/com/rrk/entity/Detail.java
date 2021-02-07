package com.rrk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author dinghao
 * @since 2021-01-15
 */
@TableName("b_detail")
public class Detail extends Model<Detail> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long mainId;
    /**
     * 品牌
     */
    private String proBrand;

    /**
     * 名称
     */
    private String proName;

    private String proNo;

    private Date createTime;


    public Long getId() {
        return id;
    }

    public Detail setId(Long id) {
        this.id = id;
        return this;
    }

    public String getProBrand() {
        return proBrand;
    }

    public Detail setProBrand(String proBrand) {
        this.proBrand = proBrand;
        return this;
    }

    public String getProName() {
        return proName;
    }

    public Detail setProName(String proName) {
        this.proName = proName;
        return this;
    }

    public String getProNo() {
        return proNo;
    }

    public Detail setProNo(String proNo) {
        this.proNo = proNo;
        return this;
    }

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Detail setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Detail{" +
        "id=" + id +
        ", proBrand=" + proBrand +
        ", proName=" + proName +
        ", proNo=" + proNo +
        ", createTime=" + createTime +
        "}";
    }
}
