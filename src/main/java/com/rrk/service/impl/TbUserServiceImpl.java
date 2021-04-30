package com.rrk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rrk.dao.TbUserMapper;
import com.rrk.dataSources.DBTypeEnum;
import com.rrk.dataSources.DataSource;
import com.rrk.entity.TbUser;
import com.rrk.service.ITbUserService;
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
 * @since 2021-02-08
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements ITbUserService {


    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private ITbUserService userService;




    @Override
    @DataSource(DBTypeEnum.db2)
    @Transactional(rollbackFor = Exception.class)
    public Integer addUser(TbUser user) {
        user.setCreateTime(new Date());
          userService.save(user);
         // int i = 1/0;
        return 1;
    }
}
