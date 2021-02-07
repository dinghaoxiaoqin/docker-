package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderNewProductDto implements Serializable {

    private String userName;

    private String productName;

    private String receiverName;

    private String productBrand;

    private BigDecimal salePrice;

    private Integer nums;
}
