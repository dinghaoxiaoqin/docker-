package com.rrk.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class TbOrder implements Serializable {

    /**
     * 订单来源
     */
    private String source;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    public enum Source{


        machine("machine","售卖机"),
        app("app","app"),
        mini("mini","小程序");

        @Getter
        public String sourceType;
        @Getter
        public String sourceName;

        Source(String sourceType,String sourceName){
            this.sourceType = sourceType;
            this.sourceName = sourceName;
        }

        public static String getSourceType(String sourceName){
            if (StrUtil.isBlank(sourceName)) {
                return null;
            } else {
                for (Source ms : Source.values()) {
                    if (sourceName.equals( ms.sourceType) ) {
                        return ms.getSourceType();
                    }
                }
                return "";
            }
        }

    }
}
