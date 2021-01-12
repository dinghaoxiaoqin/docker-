package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrendDto implements Serializable {

    /**
     * 折现图名称
     */
    private String name;

    private List<Integer> userCounts;
}
