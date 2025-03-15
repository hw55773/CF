package com.kxdkcf.constant.ai;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.constant
 * Author:              wenhao
 * CreateTime:          2025-01-01  14:35
 * Description:         TODO
 * Version:             1.0
 */
public enum Module {

    lite("lite"),
    generalv3("generalv3"),
    pro_128k("pro-128k"),
    generalv3_5("generalv3.5"),
    max_32k("max-32k"),
    Ultra_4("4.0Ultra");
    private final String value;

    Module(String value) {

        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
