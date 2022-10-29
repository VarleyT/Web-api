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
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author VarleyT
 * @date 2022/9/11
 */
@Slf4j
@RestController
@RequestMapping(value = "/API")
public class ApiController {
    /**
     * 抖音视频解析
     *
     * @param url
     * @return
     */
    @LimitRequest
    @PostMapping("/douyin")
    public R<DouYin> douyin(@RequestBody @NotEmpty String url, HttpServletResponse response) {
        final String REGEX = "https://v\\.douyin\\.com/\\w{7}/";
        List<String> list = ReUtil.findAll(REGEX, url, 0);
        if (list.isEmpty()) {
            log.info("不是一个正确的链接：" + url);
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
            return R.error("请输入正确的链接");
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
