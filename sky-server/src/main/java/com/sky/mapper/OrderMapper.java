package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {
    //插入订单信息
    void insert(Orders orders);

    //查询订单
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    //根据id查询订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    //更新订单信息
    void update(Orders orders);

    //统计订单状态
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}
