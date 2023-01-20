package online.fycloud.webapi.common.data.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 游戏白嫖信息
 *
 * @author VarleyT
 */
@Data
@AllArgsConstructor
@TableName(value = "free_game")
public class FreeGame implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    @JSONField(serialize = false)
    private Integer id;

    /**
     * 游戏名
     */
    @JSONField(ordinal = 1)
    private String gameName;

    /**
     * 链接
     */
    @JSONField(ordinal = 3)
    private String url;

    /**
     * 类型
     */
    @JSONField(ordinal = 4)
    private String type;

    /**
     * 开始时间
     */
    @JSONField(ordinal = 5, format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField(ordinal = 6, format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 是否永久
     */
    @JSONField(ordinal = 7, name = "forever")
    private boolean valid;

    /**
     * 游戏平台
     */
    @JSONField(ordinal = 2)
    private String store;

    @TableField(exist = false)
    @JSONField(serialize = false)
    private static final long serialVersionUID = 1L;
}