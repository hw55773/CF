package com.kxdkcf.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.dto
 * Author:              wenhao
 * CreateTime:          2025-03-16  15:30
 * Description:         TODO
 * Version:             1.0
 */
@Data
public class HealthDataDTO {

    /**
     * 年龄
     */
    private Integer age;
    /**
     * 性别：0：女，1：男
     */
    private Integer gender;
    /**
     * 身高
     */
    private Double height;
    /**
     * 体重
     */
    private Double weight;
    /**
     * 血压：收缩压/舒张压
     */
    private String bloodPressure;
    /**
     * 视力：5.0或1.0/0.8
     */
    private String vision;
    /**
     * 附件文件
     */
    private List<MultipartFile> files;


    public String toString() {
        return
                "年龄=" + age + '\n' +
                        "性别=" + (gender == 0 ? "女" : "男") + '\n' +
                        "身高=" + height + "cm" + '\n' +
                        "体重=" + weight + "kg" + '\n' +
                        "血压=" + bloodPressure + '\n' +
                        "视力=" + vision + '\n';
    }
}
