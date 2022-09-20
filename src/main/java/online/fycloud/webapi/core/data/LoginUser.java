package online.fycloud.webapi.core.data;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author VarleyT
 * @date 2022/9/20
 */
@Data
@Accessors(chain = true)
public class LoginUser {
    private Integer id;
    /**
     * 用户名
     */
    private String userName;
}
