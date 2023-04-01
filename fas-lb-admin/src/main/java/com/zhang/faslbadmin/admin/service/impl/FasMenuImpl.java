package com.zhang.faslbadmin.admin.service.impl;

import com.zhang.faslbadmin.admin.mapper.FasMenuMapper;
import com.zhang.faslbadmin.admin.model.po.FasMenu;
import com.zhang.faslbadmin.admin.service.FasMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/19  18:36
 */
@Service
public class FasMenuImpl implements FasMenuService {

   @Resource
    FasMenuMapper menuMapper;

    @Override
    public List<FasMenu> listAll() {
        return menuMapper.selectAll();
    }
}
