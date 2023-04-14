package com.zhang.faslbadmin.admin.controller;

import com.zhang.faslbadmin.admin.model.dto.FasMenuUpdateAndAddDto;
import com.zhang.faslbadmin.admin.service.FasMenuService;
import com.zhangyh.FasLB.model.FasMenu;
import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.common.http.respose.BaseResponse;
import com.zhangyh.common.http.respose.ResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangyh
 * @Date 2023/4/14 17:12
 * @desc
 */
@RestController
@RequestMapping("/menuManger")
public class MenuController {

    @Resource
    FasMenuService menuService;

    @GetMapping("/listAll")
    public BaseResponse<List<FasMenu>> getAllMenu() {
        List<FasMenu> fasMenus = menuService.listAll();
        return ResponseHelper.success(fasMenus);
    }

    @GetMapping("/get")
    public BaseResponse<FasMenu> getById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        FasMenu menu = menuService.getById(id);
        return ResponseHelper.success(menu);
    }

    @PostMapping("/updateMenu")
    public BaseResponse<Boolean> updateMenu(@RequestBody @Validated FasMenuUpdateAndAddDto fasMenu) {
        Boolean aBoolean = menuService.updateMenu(fasMenu);
        return ResponseHelper.success(aBoolean);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Integer> deleteMenu(@RequestParam String ids) {
        if (StringUtils.isBlank(ids)) {
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        Integer deleteNum = menuService.deleteMenu(ids);
        return ResponseHelper.success(deleteNum);
    }

    @PostMapping("/addMenu")
    public BaseResponse<Integer> addMenu(@RequestBody @Validated FasMenuUpdateAndAddDto menu) {
        return ResponseHelper.success(menuService.addMenu(menu));
    }
}
