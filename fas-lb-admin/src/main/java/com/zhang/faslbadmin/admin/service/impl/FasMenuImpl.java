package com.zhang.faslbadmin.admin.service.impl;

import com.zhang.faslbadmin.admin.model.dto.FasMenuUpdateAndAddDto;
import com.zhang.faslbadmin.admin.service.FasMenuService;
import com.zhangyh.FasLB.mapper.FasMenuMapper;
import com.zhangyh.FasLB.model.FasMenu;
import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.security.util.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        Example example = new Example(FasMenu.class);
        example.createCriteria()
                .andEqualTo(FasMenu.DELETED,0);
        return menuMapper.selectByExample(example);
    }

    public FasMenu getById(String id){
        return menuMapper.selectByPrimaryKey(Integer.parseInt(id));
    }

    @Override
    public Boolean updateMenu(FasMenuUpdateAndAddDto menu) {
        FasMenu dbMenu = new FasMenu();
        BeanUtils.copyProperties(menu,dbMenu);
        dbMenu.setUpdateBy(SecurityUtils.getCurrentUsername());
        dbMenu.setUpdateTime(new Date());
        int i = menuMapper.updateByPrimaryKey(dbMenu);
        if(i<=0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新菜单失败");
        }
        return true;
    }

    @Override
    public Integer deleteMenu(String ids) {
        AtomicInteger atomicInteger = new AtomicInteger();
        String[] id = ids.split(",");
        Arrays.stream(id).forEach(menuId->{
            Integer idm = Integer.valueOf(menuId);
            FasMenu menu = new FasMenu();
            menu.setUpdateTime(new Date());
            menu.setUpdateBy(SecurityUtils.getCurrentUsername());
            menu.setDeleted((byte) 1);
            menu.setMenuId(idm);
            int i = menuMapper.updateByPrimaryKeySelective(menu);
            atomicInteger.getAndAdd(i);
        });
        return atomicInteger.get();
    }

    @Override
    public Integer addMenu(FasMenuUpdateAndAddDto menu) {
        FasMenu dbMenu = new FasMenu();
        BeanUtils.copyProperties(menu,dbMenu);
        dbMenu.setCreateBy(SecurityUtils.getCurrentUsername());
        dbMenu.setCreateTime(new Date());
        return menuMapper.insert(dbMenu);
    }
}
