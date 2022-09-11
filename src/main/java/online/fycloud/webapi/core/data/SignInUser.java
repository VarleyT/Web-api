package online.fycloud.webapi.core.data;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author VarleyT
 */
@Data
@Accessors(chain = true)
public class SignInUser {
    @NotEmpty(message = "用户名为空")
    @Pattern(regexp = "^\\w+$", message = "用户名格式错误！只能是英文、数字、下划线")
    @Size(min = 4, max = 16, message = "用户名应在4-16之间")
    private String username;

    @NotEmpty(message = "密码为空")
    @Size(min = 6, max = 20, message = "密码应在6-20之间")
    private String password;
}
