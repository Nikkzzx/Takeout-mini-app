package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    //根据分类id 查询套餐的数量
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    //插入套餐数据
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    //分页查询
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    //根据id 查询套餐
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    //根据id 删除套餐
    @Delete("delete from setmeal where id = #{setmealId}")
    void deleteById(Long setmealId);

    //修改套餐
    void update(Setmeal setmeal);
}
