package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //根据菜品id 查询套餐id
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);
}
