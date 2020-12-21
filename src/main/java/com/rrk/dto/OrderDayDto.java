package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderDayDto implements Serializable {

    private String createTime;

    private Integer orderCount;

    private Integer nums;

    private BigDecimal amount;



}
