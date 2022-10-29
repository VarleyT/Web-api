package online.fycloud.webapi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import online.fycloud.webapi.common.entity.FreeGame;

import java.util.List;

/**
 * @author VarleyT
 * @date 2022/10/6
 */
public interface FreeGameService extends IService<FreeGame> {

    /**
     * 新增元素
     *
     * @param freeGameList
     */
    void add(List<FreeGame> freeGameList);

    /**
     * 获取信息
     *
     * @return
     */
    List<FreeGame> getInfos();
}
