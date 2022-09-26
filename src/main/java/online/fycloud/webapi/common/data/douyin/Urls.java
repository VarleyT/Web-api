package online.fycloud.webapi.common.data.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Urls implements Serializable {
    /**
     * 路径
     */
    private String uri;
    /**
     * 地址
     */
    private String[] url_list;
}
