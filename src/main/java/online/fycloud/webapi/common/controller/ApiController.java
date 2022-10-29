package online.fycloud.webapi.common.controller;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.common.data.douyin.DouYin;
import online.fycloud.webapi.common.data.genshin.GenShinPrayInfo;
import online.fycloud.webapi.common.data.genshin.GenShinRequestUrl;
import online.fycloud.webapi.common.entity.FreeGame;
import online.fycloud.webapi.common.logic.DouYinParse;
import online.fycloud.webapi.common.logic.GenShinAnalyse;
import online.fycloud.webapi.common.service.FreeGameService;
import online.fycloud.webapi.core.annotation.LimitRequest;
import online.fycloud.webapi.core.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author VarleyT
 * @date 2022/9/11
 */
@Slf4j
@RestController
@RequestMapping(value = {"/API", "/api"})
public class ApiController {
    /**
     * 抖音视频解析
     *
     * @param getUrl
     * @param map
     * @param response
     * @return
     */
    @LimitRequest
    @RequestMapping(value = "/douyin", method = {RequestMethod.GET, RequestMethod.POST})
    public R<DouYin> douyin(@RequestParam(name = "url", required = false) String getUrl,
                            @RequestBody(required = false) Map<String, String> map,
                            HttpServletResponse response) {
        final String REGEX = "https://v\\.douyin\\.com/\\w{7}/";
        List<String> list = null;
        if (getUrl != null && getUrl.length() > 0) {
            list = ReUtil.findAll(REGEX, getUrl, 0);
        } else {
            if (map != null && map.size() != 0) {
                if (map.containsKey("url")) {
                    String url = map.get("url");
                    list = ReUtil.findAll(REGEX, url, 0);
                }
            } else {
                response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
                return R.error("缺少请求参数！");
            }
        }
        if (list != null && list.isEmpty()) {
            log.info("不是一个正确的链接：getUrl: {}, map: {}", getUrl, map);
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
            return R.error("错误的抖音链接！请检查链接是否正确或者是否包含（#、&）");
        }
        String originalUrl = list.get(0);
        DouYin douyin = DouYinParse.parse(originalUrl);
        if (douyin == null) {
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
            return R.error("解析失败！");
        }
        return R.success(douyin);
    }

    /**
     * 原神抽卡分析
     * <p>
     * TODO: 2022/9/26 同一时间只能有一个用户访问
     */
    @LimitRequest
    @PostMapping("/genshin")
    public R<GenShinPrayInfo> genshin(@RequestBody @Validated GenShinRequestUrl genShinRequestUrl,
                                      HttpServletResponse response) {
        // 添加正则校验，防止URL跳转攻击。保险起见，同时也应关闭重定向
        String url = genShinRequestUrl.getUrl();
        GenShinAnalyse genShinAnalyse = new GenShinAnalyse(url);
        GenShinPrayInfo genShinPrayInfo;
        try {
            genShinPrayInfo = genShinAnalyse.analyse();
        } catch (Exception e) {
            log.error("原神抽卡分析发生错误：{}", e.toString());
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
            return R.error("获取数据失败，请检查链接是否有效，或者稍后重试");
        }
        return R.success(genShinPrayInfo);
    }

    @Autowired
    private FreeGameService freeGameService;

    @GetMapping("/freegame")
    public String freeGame() {
        List<FreeGame> list = freeGameService.getInfos();
        return JSON.toJSONString(R.success(list), SerializerFeature.WriteNullListAsEmpty);
    }
}
