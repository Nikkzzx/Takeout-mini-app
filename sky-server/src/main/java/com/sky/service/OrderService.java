package com.sky.service;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {
    //用户下单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    //历史订单查询
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    //查询订单详情
    OrderVO details(Long id);

    //取消订单
    void userCancelById(Long id) throws Exception;

    //再来一单
    void repetition(Long id);

    //搜索订单
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    //各个订单状态对应的数量
    OrderStatisticsVO statistics();

    //接单
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    //拒单
    //void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;
}
