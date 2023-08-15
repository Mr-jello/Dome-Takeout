package top.mrjello.service;


import top.mrjello.vo.BusinessDataVO;
import top.mrjello.vo.DishOverViewVO;
import top.mrjello.vo.OrderOverViewVO;
import top.mrjello.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return BusinessDataVO
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);


    /**
     * 查询订单管理数据
     * @return OrderOverViewVO
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     * @return DishOverViewVO
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览
     * @return SetmealOverViewVO
     */
    SetmealOverViewVO getSetmealOverView();

}
