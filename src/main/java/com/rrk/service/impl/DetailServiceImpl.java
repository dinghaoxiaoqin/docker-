package com.rrk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.dao.DetailMapper;
import com.rrk.entity.Detail;
import com.rrk.service.IDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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


    @Autowired
    private IDetailService detailService;


    //@DataSource(DBTypeEnum.db1)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addDetail() {
        Detail detail = new Detail();
        detail.setMainId(1L);
        detail.setProBrand("qqqq");
        detail.setProName("商品");
        detail.setProNo("1111");
        detail.setCreateTime(new Date());
        detailService.save(detail);
    }
}
