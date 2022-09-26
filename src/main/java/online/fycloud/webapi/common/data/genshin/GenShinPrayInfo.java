package online.fycloud.webapi.common.data.genshin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VarleyT
 * @date 2022/9/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenShinPrayInfo {
    /**
     * 玩家UID
     */
    private long uid;
    /**
     * 总计祈愿次数
     */
    private int total;
    /**
     * 祈愿池信息
     */
    private List<PrayPoolInfo> list;
}


