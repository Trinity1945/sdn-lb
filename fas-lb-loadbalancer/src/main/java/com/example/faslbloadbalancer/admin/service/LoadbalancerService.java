package com.example.faslbloadbalancer.admin.service;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.Resource;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/18  10:05
 */
public class LoadbalancerService implements RootService{
    @Resource
    LocalValidatorFactoryBean validator;


}
