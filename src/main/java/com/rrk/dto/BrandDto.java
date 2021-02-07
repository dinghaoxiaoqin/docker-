package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandDto implements Serializable {

    private String brandName;

    private Integer brandCount;
}
