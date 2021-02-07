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
@TableName("b_main")
public class Main extends Model<Main> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 是否提交（0，未提交，1已经提交）
     */
    private Integer isSubmit;

    /**
     * 创建时间
     */
    private Date createTime;


    public Long getId() {
        return id;
    }

    public Main setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getIsSubmit() {
        return isSubmit;
    }

    public Main setIsSubmit(Integer isSubmit) {
        this.isSubmit = isSubmit;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Main{" +
        "id=" + id +
        ", isSubmit=" + isSubmit +
        ", createTime=" + createTime +
        "}";
    }
}
