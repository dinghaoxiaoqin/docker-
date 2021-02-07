package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProvinceDto implements Serializable {

    private String provinceName;

    private Integer provinceCount;

    private List<BrandDto> brandDtos;
}
