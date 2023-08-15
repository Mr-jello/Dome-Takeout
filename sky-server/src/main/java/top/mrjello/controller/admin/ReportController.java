package top.mrjello.controller.admin;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.mrjello.result.Result;
import top.mrjello.service.ReportService;
import top.mrjello.service.WorkspaceService;
import top.mrjello.vo.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 14:35
 */
@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private WorkspaceService workspaceService;



    /**
     * 营业额统计
     * @param  begin 开始日期
     * @param end 结束日期
     * @return Result<TurnoverReportVO>
     */
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计{}到{}的营业额", begin, end);
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }
    /**
     * 用户统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return Result<TurnoverReportVO>
     */
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计{}到{}的用户", begin, end);
        UserReportVO userReportVO = reportService.userStatistics(begin, end);
        return Result.success(userReportVO);
    }

    /**
     * 订单统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return Result<OrderReportVO>
     */
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计{}到{}的订单", begin, end);
        OrderReportVO orderReportVO = reportService.ordersStatistics(begin, end);
        return Result.success(orderReportVO);
    }

    /**
     * 销量前十的统计
     */
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> salesTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计{}到{}的销量前十", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.salesTop10(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * export excel 报表导出
     * 以流的形式导出近30天的报表
     */
    @GetMapping("/export")
    public void exportExcel() {
        log.info("导出到的报表");
        //1.获取近30天的日期列表
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);

        String rangeDate = startDate.toString() + " --- " + endDate.toString();

        LocalDateTime begin = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);
        //2.数据概览
        BusinessDataVO businessData = workspaceService.getBusinessData(begin, end);

        //3.加载Excel模板文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/运营数据报表模板.xlsx");

        if (inputStream == null) {
            throw new RuntimeException("运营数据报表模板文件不存在");
        }

        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            //4.定位单元格填充数据
            Sheet sheet = workbook.getSheetAt(0);
            //4.1填充日期范围:第二行第二列
            sheet.getRow(1).getCell(1).setCellValue(rangeDate);
            //4.2填充营业额：第四行第三列
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover().doubleValue());
            //4.3填充订单完成率：第四行第五列
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            //4.4填充新增用户数：第四行第七列
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            //4.5填充有效订单：第五行第三列
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            //4.6填充平均客单价：第五行第五列
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            //5.每一天明细数据
            for (int i = 0; i < 30; i++) {
                //5.1获取每一天的数据
                LocalDate everyDate = startDate.plusDays(i);
                LocalDateTime everyBegin = LocalDateTime.of(everyDate, LocalTime.MIN);
                LocalDateTime everyEnd = LocalDateTime.of(everyDate, LocalTime.MAX);
                BusinessDataVO everyDayData = workspaceService.getBusinessData(everyBegin, everyEnd);
                //5.2填充每一天的数据
                Row row = sheet.getRow(7 + i);
                //5.2.1填充日期
                row.getCell(1).setCellValue(everyDate.toString());
                //5.2.2填充营业额
                row.getCell(2).setCellValue(everyDayData.getTurnover().doubleValue());
                //5.2.3填充有效订单
                row.getCell(3).setCellValue(everyDayData.getValidOrderCount());
                //5.2.4填充订单完成率
                row.getCell(4).setCellValue(everyDayData.getOrderCompletionRate());
                //5.2.5填充平均客单价
                row.getCell(5).setCellValue(everyDayData.getUnitPrice());
                //5.2.6填充新增用户数
                row.getCell(6).setCellValue(everyDayData.getNewUsers());
            }


            //6.导出Excel文件
            HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
            assert response != null;
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.builder("attachment")
                            .filename("运营数据报表.xlsx", StandardCharsets.UTF_8)
                            .build().toString());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM.toString());

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

            //7.释放资源
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
