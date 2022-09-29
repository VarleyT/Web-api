package online.fycloud.webapi.common.logic;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import online.fycloud.webapi.common.data.genshin.GenShinPrayInfo;
import online.fycloud.webapi.common.data.genshin.PrayItemInfo;
import online.fycloud.webapi.common.data.genshin.PrayPoolInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author VarleyT
 * @date 2022/9/25
 */

public class GenShinAnalyse {
    /**
     * 延时，过短会导致data数据为空
     */
    private final int SLEEP_TIME = 500;
    /**
     * 封装数据
     */
    private GenShinPrayInfo genShinPrayInfo;
    /**
     * 请求链接
     */
    private GenShinUrl genShinUrl;
    /**
     * 全部祈愿池
     */
    private List<PrayPoolInfo> prayPoolInfos;
    /**
     * 计数器
     */
    private int count;
    /**
     * 抽卡次数记录
     */
    private List<Integer> arrList;

    public GenShinAnalyse(String url) {
        genShinUrl = new GenShinUrl(url);
        prayPoolInfos = new ArrayList<>();
        genShinPrayInfo = new GenShinPrayInfo();
        arrList = new ArrayList<>();
    }

    public GenShinPrayInfo analyse() {
        int prayTotal = 0;
        PrayPoolInfo novicePool = getPoolInfo("新手祈愿", GachaType.NOVICE_POOL);
        PrayPoolInfo rolePool = getPoolInfo("角色祈愿", GachaType.UP_ROLE_POOL);
        PrayPoolInfo armPool = getPoolInfo("武器祈愿", GachaType.UP_ARM_POOL);
        PrayPoolInfo permPool = getPoolInfo("常驻祈愿", GachaType.PERM_POOL);
        prayPoolInfos.add(novicePool);
        prayPoolInfos.add(rolePool);
        prayPoolInfos.add(armPool);
        prayPoolInfos.add(permPool);
        for (PrayPoolInfo prayPoolInfo : prayPoolInfos) {
            prayTotal += prayPoolInfo.getTotal();
        }
        genShinPrayInfo.setList(prayPoolInfos);
        genShinPrayInfo.setTotal(prayTotal);
        genShinPrayInfo.setUid(getUID());
        return genShinPrayInfo;
    }

    private PrayPoolInfo getPoolInfo(String name, GachaType gachaType) {
        PrayPoolInfo prayPoolInfo = new PrayPoolInfo();
        prayPoolInfo.setName(name);
        prayPoolInfo.setGacha_type(gachaType.VALUE);

        genShinUrl.setGachaType(gachaType);
        genShinUrl.setEndId("0");
        do {
            String url = genShinUrl.getUrl();
            String endId = getInfo(url, prayPoolInfo);
            if (endId == null) {
                arrList.add(count);
                count = 0;
                break;
            }
            genShinUrl.nextPage(endId);
            ThreadUtil.sleep(SLEEP_TIME);
        } while (true);
        if (arrList.size() != 0) {
            prayPoolInfo.setLast(arrList.get(0));
            if (arrList.size() > 1) {
                // 五星上次抽卡记录
                for (int i = 1; i <= prayPoolInfo.getStar5_list().size(); i++) {
                    prayPoolInfo.getStar5_list().get(i - 1).setLast(arrList.get(i));
                }
            }
        }
        arrList.clear();
        prayPoolInfo.setPercent(NumberUtil.formatPercent(prayPoolInfo.getStar5_list().size() * 1.0 / prayPoolInfo.getTotal(), 2));
        return prayPoolInfo;
    }

    private String getInfo(String url, PrayPoolInfo poolInfo) {
        String id = "";
        String body = HttpUtil.createGet(url).setFollowRedirects(false).execute().body();
        Map<String, Object> result = JSON.parseObject(body);
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        List<JSONObject> list = (List<JSONObject>) data.get("list");
        for (JSONObject obj : list) {
            poolInfo.count();
            String rankType = obj.getString("rank_type");
            if (rankType.equals("3") || rankType.equals("4")) {
                count++;
            } else {
                arrList.add(count);
                String name = obj.getString("name");
                String itemType = obj.getString("item_type");
                String time = obj.getString("time");
                PrayItemInfo star5Info = new PrayItemInfo();
                star5Info.setName(name);
                star5Info.setItem_type(itemType);
                star5Info.setTime(time);
                poolInfo.addStar5Item(star5Info);
                count = 1;
            }
            id = obj.getString("id");
        }
        if (list.size() < 19) {
            id = null;
        }
        return id;
    }

    private long getUID() {
        for (GachaType gachaType : GachaType.values()) {
            genShinUrl.setGachaType(gachaType);
            try {
                String body = HttpUtil.get(genShinUrl.getUrl());
                Map<String, Object> result = JSON.parseObject(body);
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                List<JSONObject> list = (List<JSONObject>) data.get("list");
                Long uid = Long.valueOf(list.get(0).getString("uid"));
                if (uid != 0) {
                    return uid;
                }
            } catch (Exception e) {

            }
        }
        return 0;
    }

    enum GachaType {
        /**
         * 新手池
         */
        NOVICE_POOL(100),
        /**
         * 常驻池
         */
        PERM_POOL(200),
        /**
         * 角色UP池
         */
        UP_ROLE_POOL(301),
        /**
         * 武器UP池
         */
        UP_ARM_POOL(302);
        int VALUE;

        GachaType(int VALUE) {
            this.VALUE = VALUE;
        }

    }
}

class GenShinUrl {
    private String base = "https://hk4e-api.mihoyo.com/event/gacha_info/api/getGachaLog?";
    /**
     * 用户身份标识（一个authkey对应一个账户）
     */
    private String authkey;
    /**
     * 用户身份类型
     */
    private int authkey_ver;
    /**
     * 当前系统语言
     */
    private String lang;
    /**
     * 祈愿类型（"新手祈愿"，100），（"常驻祈愿"， 200），（"活动祈愿1&2"，301 )，（"武器祈愿"，302）
     */
    private int gacha_type;
    /**
     * 分页参数，当前页码
     */
    private int page = 1;
    /**
     * 分页参数，每一页的大小 最大：20
     */
    private int size = 20;
    /**
     * 查询起始数据编码，此次分页请求会从这个ID开始查询，默认为0时，查询所有数据
     */
    private String end_id = "0";

    public GenShinUrl(String url) {
        String params = url.split("\\?")[1].split("#")[0];
        this.base += params;
    }

    public String getUrl() {
        String params = new StringJoiner("&", "&", "")
                .add("gacha_type=" + gacha_type)
                .add("page=" + page)
                .add("size=" + size)
                .add("end_id=" + end_id).toString();
        return base + params;
    }

    public void setEndId(String end_id) {
        this.end_id = end_id;
    }

    public void setGachaType(GenShinAnalyse.GachaType gachaType) {
        this.gacha_type = gachaType.VALUE;
    }

    public String nextPage(String endId) {
        this.end_id = endId;
        return getUrl();
    }

}