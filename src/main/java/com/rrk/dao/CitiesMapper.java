package com.rrk.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rrk.entity.Cities;
import org.apache.ibatis.annotations.Param;

import java.util.Map;


/**
 * <p>
 * 行政区域地州市信息表 Mapper 接口
 * </p>
 *
 * @author dinghao
 * @since 2020-12-08
 */
public interface CitiesMapper extends BaseMapper<Cities> {

    Map<String,String> getCity(@Param("cityId") Integer cityId);
}
