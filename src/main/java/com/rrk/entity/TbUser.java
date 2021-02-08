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
 * @since 2021-02-08
 */
@TableName("tb_user")
public class TbUser extends Model<TbUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String userName;

    private Date createTime;


    public Long getId() {
        return id;
    }

    public TbUser setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public TbUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public TbUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public TbUser setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "TbUser{" +
        "id=" + id +
        ", userId=" + userId +
        ", userName=" + userName +
        ", createTime=" + createTime +
        "}";
    }
}
