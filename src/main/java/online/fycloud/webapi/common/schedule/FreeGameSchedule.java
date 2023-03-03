package online.fycloud.webapi.common.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.common.data.entity.FreeGame;
import online.fycloud.webapi.common.service.FreeGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author VarleyT
 * @date 2022/10/6
 */
@Slf4j
@Component
public class FreeGameSchedule {
    private static final List<String> imgKeyWords = new ArrayList<>() {{
        add("Thumbnail");
        add("VaultOpened");
        add("DieselStoreFrontWide");
        add("OfferImageWide");
    }};
    private static final String API = "https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions?locale=zh-CN&country=CN&allowCountries=CN";

    @Autowired
    private FreeGameService freeGameService;

    @Async
    @Scheduled(cron = "0 0 1 ? * 6")
    public void analyse() {
        log.info("开始执行定时任务：{}", "获取游戏白嫖信息");
        HttpResponse response = HttpUtil.createGet(API)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Safari/537.36 Edg/91.0.864.48")
                .header("Referer", "https://www.epicgames.com/store/zh-CN/")
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(10000)
                .execute();
        JSONObject result = JSON.parseObject(response.body());
        JSONArray itemArray = result.getJSONObject("data").getJSONObject("Catalog").getJSONObject("searchStore").getJSONArray("elements");
        List<FreeGame> freeGameList = new LinkedList<>();
        for (Object obj : itemArray) {
            JSONObject item = (JSONObject) obj;
            try {
                JSONObject promotions = item.getJSONObject("promotions");
                if (promotions == null) {
                    continue;
                }
                JSONArray promotionalOffers = promotions.getJSONArray("promotionalOffers");
                if (promotionalOffers != null && promotionalOffers.size() != 0) {
                    int discountPrice = item.getJSONObject("price").getJSONObject("totalPrice")
                            .getJSONObject("fmtPrice").getIntValue("discountPrice");
                    if (discountPrice == 0) {
                        FreeGame info = getInfo(item);
                        freeGameList.add(info);
                    }
                }
            } catch (Exception e) {
                log.error("{}时失败！Caused by:\n{}", "获取游戏白嫖信息", e);
            }
        }
        freeGameService.add(freeGameList);
        log.info("定时任务执行完毕：{}", "获取游戏白嫖信息");
    }

    private static FreeGame getInfo(JSONObject jsonObject) {
        String name = jsonObject.getString("title");
        String desc = jsonObject.getString("description");
        String imgUrl = "";
        for (Object o : jsonObject.getJSONArray("keyImages")) {
            JSONObject obj = (JSONObject) o;
            String type = obj.getString("type");
            String url = obj.getString("url");
            if (url != null && imgKeyWords.contains(type)) {
                imgUrl = url;
            }
        }
        String catalog = ((JSONObject) jsonObject.getJSONObject("catalogNs")
                .getJSONArray("mappings").get(0)).getString("pageSlug");
        String gameUrl = "https://store.epicgames.com/zh-CN/p/" + catalog;
        JSONObject timeObj = (JSONObject) ((JSONObject) jsonObject.getJSONObject("promotions")
                .getJSONArray("promotionalOffers").get(0))
                .getJSONArray("promotionalOffers").get(0);
        DateTime startTime = DateUtil.parse(timeObj.getString("startDate"))
                .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        DateTime endTime = DateUtil.parse(timeObj.getString("endDate"))
                .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return new FreeGame(null, name, desc, imgUrl, gameUrl, startTime, endTime, true);
    }
}
