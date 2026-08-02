package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired//注入菜品的 Mapper
    private DishMapper dishMapper;
    @Autowired//注入菜品口味的 Mapper
    private DishFlavorMapper dishFlavorMapper;

    //新增菜品及口味数据
    @Transactional//(事物注解，因为要操作菜品表和口味表，一起成功或者失败)
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);//属性拷贝,把dishDTO中的属性值拷贝到dish中
        //向菜品表插入1条数据
        dishMapper.insert(dish);
        //获取当前insert菜品生成的 id
        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {//如果填入了口味就插入数据
            //遍历集合
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //批量插入口味
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
