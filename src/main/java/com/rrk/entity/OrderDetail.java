package com.rrk.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 订单商品表
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
public class OrderDetail extends Model<OrderDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    private Long productId;

    private String productSrc;

    private String productName;

    private String productBrand;

    private String productNo;

    /**
     * 销售价格
     */
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    private Integer productNum;

    /**
     * 商品sku编号
     */
    private Long productSkuId;

    /**
     * 该商品经过优惠后的分解金额
     */
    private BigDecimal realAmount;


    public Long getId() {
        return id;
    }

    public OrderDetail setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderDetail setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public OrderDetail setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    public Long getProductId() {
        return productId;
    }

    public OrderDetail setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public String getProductSrc() {
        return productSrc;
    }

    public OrderDetail setProductSrc(String productSrc) {
        this.productSrc = productSrc;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public OrderDetail setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public OrderDetail setProductBrand(String productBrand) {
        this.productBrand = productBrand;
        return this;
    }

    public String getProductNo() {
        return productNo;
    }

    public OrderDetail setProductNo(String productNo) {
        this.productNo = productNo;
        return this;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public OrderDetail setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public Integer getProductNum() {
        return productNum;
    }

    public OrderDetail setProductNum(Integer productNum) {
        this.productNum = productNum;
        return this;
    }

    public Long getProductSkuId() {
        return productSkuId;
    }

    public OrderDetail setProductSkuId(Long productSkuId) {
        this.productSkuId = productSkuId;
        return this;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public OrderDetail setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
        "id=" + id +
        ", orderId=" + orderId +
        ", orderNo=" + orderNo +
        ", productId=" + productId +
        ", productSrc=" + productSrc +
        ", productName=" + productName +
        ", productBrand=" + productBrand +
        ", productNo=" + productNo +
        ", productPrice=" + productPrice +
        ", productNum=" + productNum +
        ", productSkuId=" + productSkuId +
        ", realAmount=" + realAmount +
        "}";
    }
}
