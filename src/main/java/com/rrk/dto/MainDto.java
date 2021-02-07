package com.rrk.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MainDto implements Serializable {

    private Long mainId;

    private String proBrand;

    private String proName;

    private String proNo;


}
