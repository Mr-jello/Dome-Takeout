package top.mrjello.service;

import top.mrjello.vo.OrderReportVO;
import top.mrjello.vo.SalesTop10ReportVO;
import top.mrjello.vo.TurnoverReportVO;
import top.mrjello.vo.UserReportVO;

import java.time.LocalDate;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 14:41
 */
public interface ReportService {

    /**
     * 营业额统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return TurnoverReportVO
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return UserReportVO
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return OrderReportVO
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 销售额Top10统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return SalesTop10ReportVO
     */
    SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end);

}
