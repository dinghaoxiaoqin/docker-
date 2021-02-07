package com.rrk.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rrk.dto.MainDto;
import com.rrk.dto.ProDto;
import com.rrk.entity.Main;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dinghao
 * @since 2021-01-15
 */
public interface IMainService extends IService<Main> {

    Integer updateMain(MainDto dto);

    void addOpsRedis(ProDto proDto);

    void addRedis(ProDto proDto);
}
