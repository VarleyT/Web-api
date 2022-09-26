package online.fycloud.webapi.common.data.genshin;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author VarleyT
 * @date 2022/9/26
 */
@Data
public class GenShinRequestUrl {
    @NotEmpty(message = "链接为空")
    @Pattern(regexp = "https://[^\\.=%]+\\.mihoyo\\.com/.*", message = "链接非法")
    private String url;
}
