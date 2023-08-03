package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 20:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoginDTO implements Serializable {

    private String username;

    private String password;

}
