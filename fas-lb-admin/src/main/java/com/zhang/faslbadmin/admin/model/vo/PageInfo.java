package com.zhang.faslbadmin.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/16  22:43
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo<T> {
    private Long total;
    private Integer page;
    private List<T> data;
}
