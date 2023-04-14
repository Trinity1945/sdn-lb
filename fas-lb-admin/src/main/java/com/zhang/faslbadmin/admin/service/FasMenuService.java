package com.zhang.faslbadmin.admin.service;

import com.zhang.faslbadmin.admin.model.dto.FasMenuUpdateAndAddDto;
import com.zhangyh.FasLB.model.FasMenu;

import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/19  18:23
 */
public interface FasMenuService {

    List<FasMenu> listAll();

    FasMenu getById(String id);

    Boolean  updateMenu(FasMenuUpdateAndAddDto menu);

    Integer deleteMenu(String ids);

    Integer addMenu(FasMenuUpdateAndAddDto menu);
}
