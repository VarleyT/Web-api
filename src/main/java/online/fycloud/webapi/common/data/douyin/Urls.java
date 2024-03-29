package online.fycloud.webapi.common.data.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    private List<String> url_list;
}
