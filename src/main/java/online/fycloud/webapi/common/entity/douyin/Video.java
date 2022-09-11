package online.fycloud.webapi.common.entity.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
@AllArgsConstructor
public class Video implements Serializable {
    /**
     * 播放地址
     */
    private Urls play_addr;
    /**
     * 视频封面
     */
    private Urls cover;
}
