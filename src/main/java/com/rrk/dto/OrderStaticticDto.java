package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderStaticticDto implements Serializable {

    private String brand;

    private Integer orderCount;

    private Integer nums;

    private BigDecimal amount;
}
