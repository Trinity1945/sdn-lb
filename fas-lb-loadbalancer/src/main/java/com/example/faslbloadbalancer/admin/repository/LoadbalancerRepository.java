package com.example.faslbloadbalancer.admin.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/18  11:58
 */
@Repository
public interface LoadbalancerRepository extends ReactiveCrudRepository<String, Integer> {
}
