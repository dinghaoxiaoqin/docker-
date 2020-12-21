package com.rrk.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Item  implements Serializable {
    private Long id;
    private Long spu;
    private Long sku;
    private String title;
    private Double price;
    private String pic;
    private String url;
    private Date created;
    private Date updated;

}
