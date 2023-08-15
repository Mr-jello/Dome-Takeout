package top.mrjello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mrjello.dto.SalesTop10ReportDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 14:42
 */
@Mapper
public interface ReportMapper {

    /**
     * 销售额Top10统计
     * @param startDateTime 开始日期
     * @param  endDateTime 结束日期
     * @return SalesTop10ReportDTO
     */
    @Select("select od.name as nameList,count(*) as numberList from orders o, order_detail od where o.id = od.order_id and o.order_time between #{startDateTime} and #{endDateTime} and o.status = #{status} group by od.name order by numberList desc limit 10")
    List<SalesTop10ReportDTO> querySalesTop10(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer status);
}
