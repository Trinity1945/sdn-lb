package com.zhangyh.FasLB.mapper;

import com.zhangyh.FasLB.dto.FasUserQueryDto;
import com.zhangyh.FasLB.model.FasUserAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  20:39
 */
public interface FasUserAccountMapper extends Mapper<FasUserAccount> {
    /**
     * 分页模糊查询
     * @param userQueryDto 查询对象
     * @return user
     */
    List<FasUserAccount> selectAllAccount( @Param("userQueryDto") FasUserQueryDto userQueryDto);
}
