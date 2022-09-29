package online.fycloud.webapi.common.data.genshin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author VarleyT
 * @date 2022/9/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrayItemInfo implements Serializable {
    /**
     * 物品名称
     */
    private String name;
    /**
     * 物品类型
     */
    private String item_type;
    /**
     * 获取时间
     */
    private String time;
    /**
     * 距离上次
     */
    private int last;
}
