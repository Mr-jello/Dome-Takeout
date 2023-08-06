package top.mrjello.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author jason@mrjello.top
 * @date 2023/8/5 3:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEditPasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long empId;

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}
