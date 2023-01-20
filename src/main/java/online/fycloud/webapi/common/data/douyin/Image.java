package online.fycloud.webapi.common.data.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author VarleyT
 * @date 2023/1/19
 */
@Data
@AllArgsConstructor
public class Image implements Serializable {
    /**
     * 图片集合
     */
    List<Urls> images;
}
