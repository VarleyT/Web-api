package online.fycloud.webapi.common.controller;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.common.entity.douyin.DouYin;
import online.fycloud.webapi.common.logic.DouYinParse;
import online.fycloud.webapi.core.annotation.LimitRequest;
import online.fycloud.webapi.core.common.R;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author VarleyT
 * @date 2022/9/11
 */
@Slf4j
@RestController
@RequestMapping("/API")
public class ApiController {
    @LimitRequest
    @PostMapping("/douyin")
    public R<DouYin> douyin(@RequestBody @NotEmpty String url) {
        final String REGEX = "https://v\\.douyin\\.com/\\w{7}/";
        List<String> list = ReUtil.findAll(REGEX, url, 0);
        if (list.isEmpty()) {
            log.info("不是一个正确的链接：" + url);
            return R.error("请输入正确的链接");
        }
        String originalUrl = list.get(0);
        DouYin douyin = DouYinParse.parse(originalUrl);
        if (douyin == null){
            return R.error("解析失败！");
        }
        return R.success(douyin);
    }
}
