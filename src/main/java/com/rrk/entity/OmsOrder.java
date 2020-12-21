package com.rrk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
public class OmsOrder extends Model<OmsOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户帐号
     */
    private String userName;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 应付金额（实际支付金额）
     */
    private BigDecimal payAmount;

    /**
     * 运费金额
     */
    private BigDecimal freightAmount;

    /**
     * 支付方式：0->未支付；1->支付宝；2->微信
     */
    private Integer payType;

    /**
     * 订单来源：0->PC订单；1->app订单
     */
    private Integer sourceType;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    private Integer orderStatus;

    /**
     * 订单类型：0->正常订单；1->秒杀订单
     */
    private Integer orderType;

    /**
     * 自动确认时间（天）
     */
    private Integer autoConfirmDay;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省名称
     */
    private String receiverProvince;

    /**
     * 城市名称
     */
    private String receiverCity;

    /**
     * 订单备注
     */
    private String note;

    /**
     * 确认收货状态：0->未确认；1->已确认
     */
    private Integer confirmStatus;

    /**
     * 删除状态：0->未删除；1->已删除
     */
    private Integer deleteStatus;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 发货时间
     */
    private Date deliveryTime;

    /**
     * 确认收货时间
     */
    private Date receiveTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;


    public Long getId() {
        return id;
    }

    public OmsOrder setId(Long id) {
        this.id = id;
        return this;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public OmsOrder setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public OmsOrder setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public OmsOrder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OmsOrder setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public OmsOrder setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
        return this;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public OmsOrder setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
        return this;
    }

    public Integer getPayType() {
        return payType;
    }

    public OmsOrder setPayType(Integer payType) {
        this.payType = payType;
        return this;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public OmsOrder setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public OmsOrder setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public OmsOrder setOrderType(Integer orderType) {
        this.orderType = orderType;
        return this;
    }

    public Integer getAutoConfirmDay() {
        return autoConfirmDay;
    }

    public OmsOrder setAutoConfirmDay(Integer autoConfirmDay) {
        this.autoConfirmDay = autoConfirmDay;
        return this;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public OmsOrder setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public OmsOrder setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
        return this;
    }



    public String getNote() {
        return note;
    }

    public OmsOrder setNote(String note) {
        this.note = note;
        return this;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public OmsOrder setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
        return this;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public OmsOrder setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
        return this;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public OmsOrder setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
        return this;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public OmsOrder setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
        return this;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public OmsOrder setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
        return this;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public OmsOrder setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
        return this;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public OmsOrder setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
        return this;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public OmsOrder setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "OmsOrder{" +
        "id=" + id +
        ", orderNo=" + orderNo +
        ", createTime=" + createTime +
        ", userName=" + userName +
        ", totalAmount=" + totalAmount +
        ", payAmount=" + payAmount +
        ", freightAmount=" + freightAmount +
        ", payType=" + payType +
        ", sourceType=" + sourceType +
        ", orderStatus=" + orderStatus +
        ", orderType=" + orderType +
        ", autoConfirmDay=" + autoConfirmDay +
        ", receiverName=" + receiverName +
        ", receiverPhone=" + receiverPhone +
        ", receiverProvince=" + receiverProvince +
        ", receiverCity=" + receiverCity +
        ", note=" + note +
        ", confirmStatus=" + confirmStatus +
        ", deleteStatus=" + deleteStatus +
        ", paymentTime=" + paymentTime +
        ", deliveryTime=" + deliveryTime +
        ", receiveTime=" + receiveTime +
        ", modifyTime=" + modifyTime +
        "}";
    }
}
