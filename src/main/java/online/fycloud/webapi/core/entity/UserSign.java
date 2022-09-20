package online.fycloud.webapi.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户签到
 *
 * @author VarleyT
 * @date 2022/9/20
 */
@Data
@AllArgsConstructor
@TableName(value = "sign")
public class UserSign implements Serializable {
    private Integer id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 签到次数
     */
    private Integer frequency;
    /**
     * 签到时间
     */
    private Date signTime;

    public static final long serialVersionUID = 1L;
}
