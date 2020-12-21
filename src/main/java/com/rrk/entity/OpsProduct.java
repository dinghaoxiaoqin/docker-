package com.rrk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author dinghao
 * @since 2020-12-07
 */
public class OpsProduct extends Model<OpsProduct> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品头像
     */
    private String productSrc;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 品牌名称
     */
    private String productBrand;

    /**
     * 品牌编码
     */
    private String productNo;

    /**
     * 价格
     */
    private BigDecimal productPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销售量
     */
    private Integer saleCount;

    /**
     * 售价
     */
    private BigDecimal amount;


    public Long getId() {
        return id;
    }

    public OpsProduct setId(Long id) {
        this.id = id;
        return this;
    }

    public String getProductSrc() {
        return productSrc;
    }

    public OpsProduct setProductSrc(String productSrc) {
        this.productSrc = productSrc;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public OpsProduct setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public OpsProduct setProductBrand(String productBrand) {
        this.productBrand = productBrand;
        return this;
    }

    public String getProductNo() {
        return productNo;
    }

    public OpsProduct setProductNo(String productNo) {
        this.productNo = productNo;
        return this;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public OpsProduct setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public Integer getStock() {
        return stock;
    }

    public OpsProduct setStock(Integer stock) {
        this.stock = stock;
        return this;
    }

    public Integer getSaleCount() {
        return saleCount;
    }

    public OpsProduct setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OpsProduct setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "OpsProduct{" +
        "id=" + id +
        ", productSrc=" + productSrc +
        ", productName=" + productName +
        ", productBrand=" + productBrand +
        ", productNo=" + productNo +
        ", productPrice=" + productPrice +
        ", stock=" + stock +
        ", saleCount=" + saleCount +
        ", amount=" + amount +
        "}";
    }
}
