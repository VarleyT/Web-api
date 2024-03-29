package online.fycloud.webapi.common.data.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Music implements Serializable {
    /**
     * 作品标题
     */
    private String title;
    /**
     * 作者名字
     */
    private String author;
    /**
     * 封面(普通质量)
     */
    private Urls cover;
    /**
     * 播放地址
     */
    private Urls play_addr;
}
