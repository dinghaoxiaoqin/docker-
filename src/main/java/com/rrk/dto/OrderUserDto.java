package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderUserDto implements Serializable {

    private String createTime;


   private Integer userTotalCount;

   private Integer userNewCount;

   private Integer userOldCount;

}
