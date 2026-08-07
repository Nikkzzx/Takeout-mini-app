package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //根据菜品id 查询套餐id
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    //插入套餐内容表里的菜品
    void insertBatch(List<SetmealDish> setmealDishes);

    //根据套餐id 删除套餐菜品关系表里的菜品
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    //根据套餐id 查询套餐内容
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long id);
}
