package com.rrk.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class EsEntity implements Serializable {

    private Long id;

    /**
     * 订单号
     */
    private String orderno;
    /**
     * 下单人名称
     */
    private String username;
    /**
     * 订单状态
     */
    private Integer orderstatus;

    /**
     * 订单类型
     */
    private Integer ordertype;
    /**
     * 确认时间
     */
    private Integer autoconfirmday;

    /**
     * 实付总金额
     */
    private BigDecimal totalamount;

    /**
     * 支付金额
     */
    private BigDecimal payamount;

    /**
     * 金额
     */
    private BigDecimal freightamount;
    /**
     * 订单来源
     */
    private Integer sourcetype;
    /**
     * 收货人名称
     */
    private String receivername;
    /**
     * 收货人电话
     */
    private String receiverphone;
    /**
     * receiverprovince 省份
     */
    private String receiverprovince;
    /**
     * 城市
     */
    private String receivercity;
    /**
     * 确认状态
     */
    private Integer confirmstatus;

    /**
     * 删除状态
     */
    private Integer deletestatus;
    /**
     * 下单时间
     */
    private Date createtime;
    /**
     * 支付时间
     */
    private Date paymenttime;

    /**
     * 发货时间
     */
    private Date deliverytime;
    /**
     * 收货时间
     */
    private Date receivetime;

    private Integer productid;

    private String productsrc;

    private String productname;

    private String productbrand;

    private BigDecimal productprice;

    private Integer productnum;

    private BigDecimal realamount;

    /**
     * 高亮的字段
     */
    private String highLight;
}
