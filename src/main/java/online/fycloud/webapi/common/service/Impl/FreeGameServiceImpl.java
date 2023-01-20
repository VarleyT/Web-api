package online.fycloud.webapi.common.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.fycloud.webapi.common.data.entity.FreeGame;
import online.fycloud.webapi.common.mapper.FreeGameMapper;
import online.fycloud.webapi.common.service.FreeGameService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author VarleyT
 * @date 2022/10/6
 */
@Service
public class FreeGameServiceImpl extends ServiceImpl<FreeGameMapper, FreeGame> implements FreeGameService {
    @Override
    public void add(List<FreeGame> freeGameList) {
        freeGameList.forEach(freeGame -> saveOrUpdate(freeGame));
    }

    @Override
    public List<FreeGame> getInfos() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        List<FreeGame> list = list(new QueryWrapper<FreeGame>()
                .apply("start_time < time({0}) and end_time > time({0})", now));
        return list;
    }


}
