package online.fycloud.webapi.common.logic;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import online.fycloud.webapi.common.data.douyin.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VarleyT
 * @date 2022/9/11
 */
public class DouYinParse {
    public static DouYin parse(String url) {
        Map<String, String> headers = new HashMap<>() {{
            put("Connection", "keep-alive");
            put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Microsoft Edge\";v=\"90\"");
            put("Accept", "*/*");
            put("X-Requested-With", "XMLHttpRequest");
            put("sec-ch-ua-mobile", "?0");
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.66");
            put("Sec-Fetch-Site", "same-origin");
            put("Sec-Fetch-Mode", "cors");
            put("Sec-Fetch-Dest", "empty");
            put("Referer", "https://www.iesdouyin.com/share/video/6561991332561161476/?region=CN&mid=6561671254439365390&u_code=0&titleType=title&did=MS4wLjABAAAA2Cy8LTQsppRk4gci9RcF18kdcuNyaQRtZcZt0BGbylg&iid=MS4wLjABAAAAWHQavP6vURszBFMcxNrThBB0wrNEDWNzLdTKiuW5cI_cOJvn7h0u20Uz8R292pd2&with_sec_did=1&utm_source=copy_link&utm_campaign=client_share&utm_medium=android&app=aweme&scheme_type=1");
            put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            put("Cookie", "MONITOR_WEB_ID=4843f090-b627-46db-bbe2-f757b4ea21a0; _tea_utm_cache_1243={%22utm_source%22:%22copy_link%22%2C%22utm_medium%22:%22android%22%2C%22utm_campaign%22:%22client_share%22}");
        }};
        HttpRequest request = HttpUtil.createGet(url)
                .addHeaders(headers)
                .setFollowRedirects(false);
        try {
            HttpResponse response = request.execute();
            String itemId = ReUtil.get("video/(\\d+)/", response.body(), 0).replaceAll("\\D", "");
            String videoUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + itemId;
            HttpRequest request1 = HttpUtil.createGet(videoUrl)
                    .addHeaders(headers);
            HttpResponse response1 = request1.execute();
            JSONObject jsonObject = JSON.parseObject(response1.body());
            JSONObject infos = jsonObject.getJSONArray("item_list").getJSONObject(0);

            String desc = infos.getString("desc");
            long create_time = Long.parseLong(infos.getString("create_time"));
            //作者
            JSONObject author = infos.getJSONObject("author");
            String nickname = author.getString("nickname");
            String signature = author.getString("signature");
            JSONObject avatar_medium = author.getJSONObject("avatar_medium");
            String avatar_uri = avatar_medium.getString("uri");
            String[] avatar_url_list = avatar_medium.getJSONArray("url_list").toArray(new String[]{});
            //音频
            JSONObject music = infos.getJSONObject("music");
            String music_title = music.getString("title");
            String music_author = music.getString("author");
            JSONObject cover_medium = music.getJSONObject("cover_medium");
            String cover_uri = cover_medium.getString("uri");
            String[] cover_url_list = avatar_medium.getJSONArray("url_list").toArray(new String[]{});
            //视频
            JSONObject video = infos.getJSONObject("video");
            JSONObject play_addr = video.getJSONObject("play_addr");
            String play_addr_uri = play_addr.getString("uri");
            String[] play_addr_url_list = play_addr.getJSONArray("url_list").toArray(new String[]{});
            JSONObject video_cover = video.getJSONObject("cover");
            String video_cover_uri = video_cover.getString("uri");
            String[] video_cover_url_list = video_cover.getJSONArray("url_list").toArray(new String[]{});
            //播放地址
            String play_url = play_addr_url_list[0].replace("playwm", "play");
            return new DouYin(desc, create_time, play_url,
                    new Author(nickname, signature, new Urls(avatar_uri, avatar_url_list)),
                    new Music(music_title, music_author, new Urls(cover_uri, cover_url_list)),
                    new Video(new Urls(play_addr_uri, play_addr_url_list), new Urls(video_cover_uri, video_cover_url_list)));
        }
        catch (Exception e){
            return null;
        }
    }
}
