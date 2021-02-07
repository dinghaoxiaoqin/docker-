package com.rrk.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.dao.DetailMapper;
import com.rrk.entity.Detail;
import com.rrk.service.IDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dinghao
 * @since 2021-01-15
 */
@Service
public class DetailServiceImpl extends ServiceImpl<DetailMapper, Detail> implements IDetailService {

}
