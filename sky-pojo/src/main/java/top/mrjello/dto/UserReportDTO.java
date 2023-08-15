package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jason@mrjello.top
 * @date 2023/8/13 17:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReportDTO implements Serializable {

    //注册日期 yyyy-MM-dd
    private String createDate;

    //用户数量
    private Integer userCount;
}
