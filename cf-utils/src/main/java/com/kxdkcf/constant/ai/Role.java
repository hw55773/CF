package com.kxdkcf.constant.ai;

public enum Role {

    user("user"),
    assistant("assistant"),
    system("system"),
    tool("tool");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {

        return this.value;
    }
}
