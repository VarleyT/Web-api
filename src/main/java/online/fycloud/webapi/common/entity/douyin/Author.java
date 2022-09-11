package online.fycloud.webapi.common.entity.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Author implements Serializable {
    /**
     * 作者名字
     */
    private String nickname;
    /**
     * 作者简介
     */
    private String signature;
    /**
     * 头像(普通质量)
     */
    private Urls avatar_medium;
}
