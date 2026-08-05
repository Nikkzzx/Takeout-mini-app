package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired//注入套餐的 Mapper
    private SetmealDishMapper setmealDishMapper;

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
    //菜品分页查询
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //pageHelper分页(当前的页码，每页数据量)
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //批量删除菜品
    @Transactional//(事物注解，因为要操作菜品表和口味表，一起成功或者失败)
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否在售，在售则不能删除
        for (Long id : ids){
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否与套餐关联，关联则不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            //有套餐关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表的菜品数据
//        for (Long id : ids){
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //批量删除菜品数据
        //sql:delete from dish where id in (?,?,?)
        dishMapper.deleteByIds(ids);
        //批量删除菜品对应的口味数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    //根据id 查询菜品和对应的口味数据
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //组装VO数据并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    //修改菜品及口味数据
    public void updateWithFlavor(DishDTO dishDTO) {
         //修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {//如果填入了口味就插入数据
            //遍历集合
            flavors.forEach(dishFlavor -> {//给每个口味设置上该菜品的id
                dishFlavor.setDishId(dishDTO.getId());
            });
            //批量插入口味（根据口味的id和菜品的id关联进行插入）
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
