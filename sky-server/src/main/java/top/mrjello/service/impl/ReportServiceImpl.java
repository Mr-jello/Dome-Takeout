package top.mrjello.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrjello.dto.OrderReportDTO;
import top.mrjello.dto.SalesTop10ReportDTO;
import top.mrjello.dto.TurnoverReportDTO;
import top.mrjello.dto.UserReportDTO;
import top.mrjello.entity.Orders;
import top.mrjello.mapper.OrdersMapper;
import top.mrjello.mapper.ReportMapper;
import top.mrjello.mapper.UserMapper;
import top.mrjello.service.ReportService;
import top.mrjello.service.WorkspaceService;
import top.mrjello.vo.OrderReportVO;
import top.mrjello.vo.SalesTop10ReportVO;
import top.mrjello.vo.TurnoverReportVO;
import top.mrjello.vo.UserReportVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 14:42
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkspaceService workspaceService;


    /**
     * 统计指定日期范围内的营业额
     * 此方法返回的TurnoverReportVO对象中String dateList和String turnoverList,用于前端展示
     * @param begin 开始日期
     * @param end 结束日期
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<String> dateList = getDateStrList(begin, end);

        //2.统计指定日期的营业额
        LocalDateTime startDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        List<TurnoverReportDTO> turnoverReportDTOList = ordersMapper.queryTurnoverStatistics(startDateTime, endDateTime, Orders.ORDER_COMPLETED);

        List<BigDecimal> turnoverList = convertDTOListToStatisticsList(turnoverReportDTOList, dateList, TurnoverReportDTO::getOrderDate, TurnoverReportDTO::getOrderMoney, BigDecimal.ZERO);

        //3.封装数据
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定日期范围内的用户数量
     * @param begin 开始日期
     * @param end 结束日期
     * @return UserReportVO
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.获取指定日期范围内日期列表
        List<String> dateList = getDateStrList(begin, end);
        LocalDateTime startDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);

        //2.统计指定日期范围内的新增用户数量
        List<UserReportDTO> userReportDTOList = userMapper.queryUserStatistics(startDateTime, endDateTime);
//        //2.1 userReportDTOList转换为Map<日期,新增用户数量>
//        Map<String, Integer> userStatisticsMap = userReportDTOList.stream().collect(Collectors.toMap(UserReportDTO::getCreateDate, UserReportDTO::getUserCount));
//        //2.2 将dateList和userReportDTOList进行匹配,如果dateList中的日期在userReportDTOList中不存在,则将该日期的新增用户数量设置为0,转为List<Integer>
//        List<Integer> userCountList = dateList.stream().map(date -> {
//            return userStatisticsMap.get(date) == null ? 0 : userStatisticsMap.get(date);
//        }).toList();
        List<Integer> userCountList = convertDTOListToStatisticsList(userReportDTOList, dateList, UserReportDTO::getCreateDate, UserReportDTO::getUserCount, 0);

        //3.统计指定日期范围内的总用户数量
        Integer baseCount = userMapper.queryTotalUserStatistics(startDateTime);
        //3.1 循环遍历userCountList,将每个日期的用户数量累加,得到每个日期的总用户数量
        List<Integer> totalUserList = new ArrayList<>();
        for (Integer add: userCountList) {
            baseCount += add;
            totalUserList.add(baseCount);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ",")) //日期列表
                .newUserList(StringUtils.join(userCountList, ",")) //新增用户列表
                .totalUserList(StringUtils.join(totalUserList, ",")) //总用户列表
                .build();
    }

    /**
     * 统计指定日期范围内的订单数量
     * @param begin 开始日期
     * @param end 结束日期
     * @return OrderReportVO
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //1.获取指定日期范围内日期列表
        List<String> dateList = getDateStrList(begin, end);
        LocalDateTime startDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        //2.统计指定范围内每日订单总数列表
        List<OrderReportDTO> orderReportDTOList = ordersMapper.queryOrdersStatistics(startDateTime, endDateTime, null);
//        //2.1 orderReportDTOList转换为Map<日期,订单总数>
//        Map<String, Integer> orderStatisticsMap = orderReportDTOList.stream().collect(Collectors.toMap(OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount));
//        //2.2 将dateList和orderReportDTOList进行匹配,如果dateList中的日期在orderReportDTOList中不存在,则将该日期的订单总数设置为0,转为List<Integer>
//        List<Integer> orderCountList = dateList.stream().map(date -> {
//            return orderStatisticsMap.get(date) == null ? 0 : orderStatisticsMap.get(date);
//        }).toList();
        List<Integer> orderCountList = convertDTOListToStatisticsList(orderReportDTOList, dateList, OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount, 0);

        //3.统计指定范围内有效订单总数列表
        List<OrderReportDTO> validOrderReportDTOList = ordersMapper.queryOrdersStatistics(startDateTime, endDateTime, Orders.ORDER_COMPLETED);
        List<Integer> validOrderCountList = convertDTOListToStatisticsList(validOrderReportDTOList, dateList, OrderReportDTO::getOrderDate, OrderReportDTO::getOrderCount, 0);

        //4.获取订单总数 -- 从orderCountList进行累加
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).isPresent() ? orderCountList.stream().reduce(Integer::sum).get() : 0;

        //5.获取有效订单总数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).isPresent() ? validOrderCountList.stream().reduce(Integer::sum).get() : 0;

        //6.计算订单完成率
        Double orderCompletionRate = totalOrderCount == 0 ? 0 : (double) validOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                            .dateList(StringUtils.join(dateList, ",")) //日期列表
                            .orderCountList(StringUtils.join(orderCountList, ",")) //订单总数列表
                            .validOrderCountList(StringUtils.join(validOrderCountList, ",")) //有效订单总数列表
                            .totalOrderCount(totalOrderCount) //订单总数
                            .validOrderCount(validOrderCount) //有效订单总数
                            .orderCompletionRate(orderCompletionRate) //订单完成率
                            .build();

    }

    /**
     * 统计指定日期范围内的销售额
     * @param begin 开始日期
     * @param end 结束日期
     * @return SalesStatisticsVO
     */
    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime startDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        //1.获取指定日期范围销量前十的商品名称列表
        List<SalesTop10ReportDTO> salesTop10ReportDTOList = reportMapper.querySalesTop10(startDateTime, endDateTime, Orders.ORDER_COMPLETED);
        List<String> nameList = salesTop10ReportDTOList.stream().map(SalesTop10ReportDTO::getNameList).toList();
        //2.获取指定日期范围销量前十的商品销量列表
        List<String> salesList = salesTop10ReportDTOList.stream().map(SalesTop10ReportDTO::getNumberList).toList();
        return SalesTop10ReportVO.builder()
                                    .nameList(StringUtils.join(nameList, ",")) //商品名称列表
                                    .numberList(StringUtils.join(salesList, ",")) //商品销量列表
                                    .build();
    }



    /**
     * 获取指定日期范围内的字符串列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return List<String>
     */
    private List<String> getDateStrList(LocalDate startDate, LocalDate endDate) {
        //1.1.获取两个日期之间的所有日期,包含开始日期和结束日期(datesUntil方法不包含结束日期)
        List<LocalDate> dateList = startDate.datesUntil(endDate.plusDays(1)).toList();
        //1.2将dateList转换为字符串列表
        return dateList.stream().map(localDate -> {
            return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }).toList();
    }

    /**
     * 将各种DTOList转换为对应的Map
     * @param dtoList DTO列表
     * @param dateList 日期列表
     * @param getDateFunction 获取日期的方法
     * @param getCountFunction 获取数量的方法
     * @param zeroValue 0
     * @param <T> DTO类型
     * @param <R> 返回值类型
     */
    public <T, R> List<R> convertDTOListToStatisticsList(List<T> dtoList, List<String> dateList, Function<T, String> getDateFunction, Function<T, R> getCountFunction, R zeroValue) {
        Map<String, R> statisticsMap = dtoList.stream().collect(Collectors.toMap(getDateFunction, getCountFunction));

        return dateList.stream().map(date -> statisticsMap.get(date) == null ? zeroValue : statisticsMap.get(date)).collect(Collectors.toList());
    }

}
