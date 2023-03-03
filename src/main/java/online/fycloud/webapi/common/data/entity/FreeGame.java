package online.fycloud.webapi.common.data.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 游戏白嫖信息
 *
 * @author VarleyT
 */
@Getter
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
    @JSONField(ordinal = 1, name = "game_name")
    private String gameName;

    /**
     * 游戏介绍
     */
    @JSONField(ordinal = 2, name = "game_desc")
    private String gameDesc;

    /**
     * 跳转链接
     */
    @JSONField(ordinal = 3, name = "store_url")
    private String storeUrl;

    /**
     * 图片链接
     */
    @JSONField(ordinal = 4, name = "img_url")
    private String imgUrl;

    /**
     * 开始时间
     */
    @JSONField(ordinal = 5, format = "yyyy-MM-dd HH:mm:ss", name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField(ordinal = 6, format = "yyyy-MM-dd HH:mm:ss", name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 有效
     */
    @JSONField(serialize = false)
    private boolean del;

    @TableField(exist = false)
    @JSONField(serialize = false)
    private static final long serialVersionUID = 1L;
}