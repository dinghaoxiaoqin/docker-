package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderBrandDto implements Serializable {

    private List<String> brandName;

    private List<Integer> orderCount;

    private List<Integer> nums;

    private List<BigDecimal> amount;

}
