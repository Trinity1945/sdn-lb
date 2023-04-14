package com.zhangyh.logging.admin.service.impl;

import com.zhangyh.FasLB.mapper.LogMapper;
import com.zhangyh.FasLB.model.Log;
import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.logging.admin.service.LogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  8:20
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    LogMapper logMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save( Log log) {
        if(log==null){
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        logMapper.insert(log);
    }

    @Override
    public List<Log> queryAll() {
        return logMapper.selectAll();
    }

    @Override
    public Log get(String id) {
        return logMapper.selectByPrimaryKey(Integer.parseInt(id));
    }


}
