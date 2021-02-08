package com.rrk.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.dao.TbUserMapper;
import com.rrk.dataSources.DataSourceAnnonation;
import com.rrk.dataSources.DataSourceNames;
import com.rrk.entity.TbUser;
import com.rrk.service.ITbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dinghao
 * @since 2021-02-08
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements ITbUserService {


    @Autowired
    private TbUserMapper userMapper;

    @DataSourceAnnonation(DataSourceNames.TWO)
    @Override
    public Integer addUser(TbUser user) {
        user.setCreateTime(new Date());
        Integer insert = userMapper.insert(user);
        return insert;
    }
}
