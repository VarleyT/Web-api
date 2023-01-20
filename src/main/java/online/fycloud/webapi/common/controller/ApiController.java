package online.fycloud.webapi.common.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.common.data.douyin.DouYin;
import online.fycloud.webapi.common.data.entity.FreeGame;
import online.fycloud.webapi.common.data.genshin.GenShinPrayInfo;
import online.fycloud.webapi.common.data.genshin.GenShinRequestUrl;
import online.fycloud.webapi.common.logic.DouYinParse;
import online.fycloud.webapi.common.logic.GenShinAnalyse;
import online.fycloud.webapi.common.service.FreeGameService;
import online.fycloud.webapi.core.annotation.LimitRequest;
import online.fycloud.webapi.core.common.R;
import online.fycloud.webapi.core.exception.ErrorCodes;
import online.fycloud.webapi.core.exception.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param url
     * @return
     * @throws ServerException
     */
    @LimitRequest
    @PostMapping("/douyin")
    public R<DouYin> douyin(@RequestBody String url) throws ServerException {
        DouYin douyin = DouYinParse.parse(url);
        return R.success(douyin);
    }

    /**
     * 原神抽卡分析
     * <p>
     * TODO: 2022/9/26 同一时间只能有一个用户访问
     */
    @LimitRequest
    @PostMapping("/genshin")
    public R<GenShinPrayInfo> genshin(@RequestBody @Validated GenShinRequestUrl genShinRequestUrl) throws ServerException {
        // 添加正则校验，防止URL跳转攻击。保险起见，同时也应关闭重定向
        String url = genShinRequestUrl.getUrl();
        GenShinAnalyse genShinAnalyse = new GenShinAnalyse(url);
        GenShinPrayInfo genShinPrayInfo;
        try {
            genShinPrayInfo = genShinAnalyse.analyse();
        } catch (Exception e) {
            log.error("原神抽卡分析发生错误：{}", e.toString());
            throw new ServerException(ErrorCodes.HANDLE_ERROR, "获取数据失败，请检查链接是否有效，或者稍后重试");
        }
        return R.success(genShinPrayInfo);
    }

    @Autowired
    private FreeGameService freeGameService;

    @GetMapping("/freegame")
    public String freeGame() {
        List<FreeGame> list = freeGameService.getInfos();
        return JSON.toJSONString(R.success(list), JSONWriter.Feature.WriteNullListAsEmpty);
    }
}
