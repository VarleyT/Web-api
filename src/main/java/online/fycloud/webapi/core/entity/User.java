package online.fycloud.webapi.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author VarleyT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    /**
     * id
     */
    public Integer id;
    /**
     * 用户名
     */
    public String userName;
    /**
     * 密码
     */
    public String passwd;
    /**
     * 创建时间
     */
    public Date createTime;
    /**
     * 状态
     */
    public Integer status;

    public static final long serialVersionUID = 1L;
}
