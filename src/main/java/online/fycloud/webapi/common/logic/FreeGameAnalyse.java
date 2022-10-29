package online.fycloud.webapi.common.logic;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.common.entity.FreeGame;
import online.fycloud.webapi.common.service.FreeGameService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author VarleyT
 * @date 2022/10/6
 */
@Slf4j
@Component
public class FreeGameAnalyse {
    private static final String API = "https://steamstats.cn/xi";

    @Autowired
    private FreeGameService freeGameService;

    @Scheduled(cron = "0 0 1 ? * 5")
    public void analyse() {
        log.info("开始执行定时任务：{}", "获取游戏白嫖信息");
        List<FreeGame> freeGameList = new LinkedList<>();
        HttpRequest request = HttpUtil.createGet(API).header("accept-language", "zh-CN,zh;q=0.9")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("accept-language", "zh-CN,zh;q=0.9")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36")
                .setFollowRedirects(false);
        Document doc = Jsoup.parse(request.execute().body());
        Elements datas = doc.getElementsByClass("v-data-table__wrapper");
        Elements gameInfos = datas.get(0).child(0).child(1).children();
        for (Element gameInfo : gameInfos) {
            String gameName = gameInfo.child(1).select("a").first().attr("title");
            String url = gameInfo.child(1).select("a").first().attr("href");
            String type = gameInfo.child(2).text();
            DateTime startTime = DateUtil.parse(gameInfo.child(3).text(), "yyyy-MM-dd");
            DateTime endTime = DateUtil.parse(gameInfo.child(4).text(), "yyyy-MM-dd")
                    .offset(DateField.HOUR, 23);
            boolean valid = false;
            if (gameInfo.child(5).text().equals("是") || gameInfo.child(5).text().equals("yes")) {
                valid = true;
            }
            String store = gameInfo.child(6).getElementsByClass("v-btn__content").get(0).text();
            freeGameList.add(new FreeGame(null, gameName, url, type, startTime, endTime, valid, store));
        }
        freeGameService.add(freeGameList);
        log.info("定时任务执行完毕：{}", "获取游戏白嫖信息");
    }
}
