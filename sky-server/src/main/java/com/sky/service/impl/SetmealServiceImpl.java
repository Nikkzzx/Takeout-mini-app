package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    //新增套餐
    @Transactional//要添加套餐的基本信息和套餐和菜品的关联关系
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //向套餐表插入数据
        setmealMapper.insert(setmeal);
        //获取生成的套餐表的id
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //循环遍历 为集合里的菜品设置该套餐的id
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //套餐内容表里插入菜品集合
        setmealDishMapper.insertBatch(setmealDishes);
    }

    //套餐分页查询
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //pageHelper分页(当前的页码，每页数据量)
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //批量删除套餐
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //循环遍历判断删除的套餐是否起售
        ids.forEach(id -> {
                    Setmeal setmeal = setmealMapper.getById(id);
                    if (StatusConstant.ENABLE == setmeal.getStatus()) {
                        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                    }
        });
        //删除
        ids.forEach(setmealId-> {
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //删除套餐菜品关系表的相关数据
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }

    //根据id查询套餐
    public SetmealVO getByIdWithDish(Long id) {
        //查询套餐数据
        Setmeal setmeal = setmealMapper.getById(id);
        //查询套餐菜品关系数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        //将查询到的数据封装成SetmealVO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    //修改套餐
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改套餐表数据
        setmealMapper.update(setmeal);
        //套餐id
        Long setmealId = setmealDTO.getId();
        //删除原来存储的套餐菜品关系数据
        setmealDishMapper.deleteBySetmealId(setmealId);
        //重新插入传进来的套餐信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //为菜品设置套餐id
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    //套餐起售停售
    public void startOrStop(Integer status, Long id) {
        //如果要起售，判断套餐内是否有停售的菜品,有则抛出异常
        if (status == StatusConstant.ENABLE) {
            //把当前套餐里的所有菜品装到集合里
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if (dishList != null && dishList.size() > 0) {
                dishList.forEach(dish -> {
                    if (dish.getStatus() == StatusConstant.DISABLE) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    //条件查询
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    //根据id查询菜品选项
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
