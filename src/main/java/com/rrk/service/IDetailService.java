package com.rrk.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rrk.entity.Detail;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dinghao
 * @since 2021-01-15
 */
public interface IDetailService extends IService<Detail> {

    void addDetail();
}
