package online.fycloud.webapi.common.data.douyin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author VarleyT
 * @date 2022/9/11
 */
@Data
@AllArgsConstructor
public class DouYin implements Serializable {
    /**
     * 视频简介
     */
    private String desc;
    /**
     * 上传时间
     */
    private long create_time;
    /**
     * 作者
     */
    private Author author;
    /**
     * 音频
     */
    private Music music;
    /**
     * 视频
     */
    private Video video;
    /**
     * 图片
     */
    private Image image;
}