package online.fycloud.webapi.common.logic;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.common.data.douyin.*;
import online.fycloud.webapi.core.exception.ServerException;

import java.util.*;

/**
 * @author VarleyT
 * @date 2022/9/11
 */
@Slf4j
public class DouYinParse {

    private static final String VIDEO_API = "https://www.iesdouyin.com/aweme/v1/web/aweme/detail/?aweme_id=%s&aid=1128&version_name=23.5.0&device_platform=android&os_version=2333&Github=Evil0ctal&words=FXXK_U_ByteDance";
    private static final Map<String, String> HEADERS = new HashMap<>() {{
        put("User-Agent", "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36 Edg/87.0.664.66");
    }};

    // 图片类型代码
    private static final List<Integer> IMAGE_TYPE_LIST = new ArrayList<>() {{
        add(2);
        add(68);
    }};

    public static DouYin parse(String url) throws ServerException {
        String videoId = getVideoId(url);
        if (videoId == null || videoId.isEmpty()) {
            throw new ServerException("未获取到视频信息，请检查链接是否正确");
        }
        HttpResponse response = HttpUtil.createGet(String.format(VIDEO_API, videoId))
                .addHeaders(HEADERS)
                .setFollowRedirects(false)
                .execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        if (jsonObject == null) {
            throw new ServerException("解析视频失败，请检查api是否失效");
        }
        boolean isVideo = true;
        String desc = null;
        long createTime = 0L;
        Author author = null;
        Music music = null;
        Video video = null;
        Image image = null;
        try {
            JSONObject detail = jsonObject.getJSONObject("aweme_detail");
            int aweme_type = detail.getIntValue("aweme_type");
            if (IMAGE_TYPE_LIST.contains(aweme_type)) {
                isVideo = false;
            }

            // 作者
            JSONObject authorObj = detail.getJSONObject("author");
            JSONObject authorAvatar = authorObj.getJSONObject("avatar_thumb");
            String authorUri = authorAvatar.getString("uri");
            List<String> authorUrlList = authorAvatar.getList("url_list", String.class);
            String authorNickname = authorObj.getString("nickname");
            String authorSignature = authorObj.getString("signature");
            author = new Author(authorNickname, authorSignature, new Urls(authorUri, authorUrlList));

            if (isVideo) {
                // 视频
                JSONObject videoObj = detail.getJSONObject("video");
                JSONObject videoPlayAddr = videoObj.getJSONObject("play_addr");
                String videoUri = videoPlayAddr.getString("uri");
                List<String> videoUrlList = videoPlayAddr.getList("url_list", String.class);
                JSONObject videoCoverObj = videoObj.getJSONObject("cover");
                String videoCoverUri = videoCoverObj.getString("uri");
                List<String> videoCoverUrlList = videoCoverObj.getList("url_list", String.class);
                video = new Video(new Urls(videoUri, videoUrlList), new Urls(videoCoverUri, videoCoverUrlList));

                desc = detail.getString("desc");
                createTime = detail.getLongValue("create_time");
            } else {
                // 图片
                List<Urls> imageList = new LinkedList<>();
                JSONArray images = detail.getJSONArray("images");
                for (Object obj : images) {
                    JSONObject object = (JSONObject) obj;
                    String imageUri = object.getString("uri");
                    List<String> imageUrlList = object.getList("url_list", String.class);
                    imageList.add(new Urls(imageUri, imageUrlList));
                }
                image = new Image(imageList);
            }

            // 音乐
            JSONObject musicObj = detail.getJSONObject("music");
            String musicTitle = musicObj.getString("title");
            String musicAuthor = musicObj.getString("author");
            JSONObject musicAvatar = musicObj.getJSONObject("avatar_medium");
            // 图文
            if (musicAvatar != null) {
                String musicUri = musicAvatar.getString("uri");
                List<String> musicUrlList = musicAvatar.getList("url_list", String.class);
                music = new Music(musicTitle, musicAuthor, new Urls(musicUri, musicUrlList));
            } else { // 笔记note
                JSONObject musicPlayAddr = musicObj.getJSONObject("play_url");
                String musicUri = musicPlayAddr.getString("uri");
                List<String> musicUrlList = musicPlayAddr.getList("url_list", String.class);
                music = new Music(musicTitle, musicAuthor, new Urls(musicUri, musicUrlList));
            }
        } catch (Exception e) {
            log.error("解析抖音视频时出错！Cased by: \n{}", e.getMessage());
        }
        return new DouYin(desc, createTime, author, music, video, image);
    }

    private static String getVideoId(String originalUrl) {
        String shortUrl = ReUtil.get("https://v\\.douyin\\.com/\\w{7}/", originalUrl, 0);
        if (shortUrl == null || shortUrl.isEmpty()) {
            return null;
        }
        HttpResponse response = HttpUtil.createGet(shortUrl)
                .addHeaders(HEADERS)
                .setFollowRedirects(false)
                .execute();
        if (response.getStatus() == HttpStatus.HTTP_MOVED_TEMP) {
            String location = response.header("Location");
            if (location != null && !location.isEmpty()) {
                String videoId = ReUtil.get("(?<=/video/)\\d+(?=/)", location, 0);
                if (videoId != null && !videoId.isEmpty()) {
                    return videoId;
                }
            }
        }
        return null;
    }
}
