package online.fycloud.webapi.common.data.genshin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VarleyT
 * @date 2022/9/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrayPoolInfo {
    /**
     * 祈愿池名称
     */
    private String name;
    /**
     * 祈愿池编号 （"新手祈愿"，100），（"常驻祈愿"， 200），（"活动祈愿1&2"，301 )，（"武器祈愿"，302）
     */
    private int gacha_type;
    /**
     * 总计次数
     */
    private int total;
    /**
     * 综合概率
     */
    private String percent;
    /**
     * 5星信息
     */
    private List<PrayItemInfo> star5_list = new ArrayList<>();

    public void count() {
        this.total += 1;
    }

    public void addStar5Item(PrayItemInfo prayItemInfo) {
        star5_list.add(prayItemInfo);
    }

}

