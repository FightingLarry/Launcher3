package com.dh.home;

// v2.1 应用类型
public enum AppType {
    /** Hotseat */
    PHONE("PHONE"), CONTACTS("CONTACTS"), BROWSER("BROWSER"), MMS("MMS"), //

    CLOCK("CLOCK"),
    /** 日历 */
    CALENDAR("CALENDAR"),
    /** 画廊 */
    GALLERY("GALLERY"),
    /** 邮件 */
    EMAIL("EMAIL"),
    /** 下载 */
    DOWNLOADS("DOWNLOADS"),

    /** 计算器 */
    CALCULATOR("CALCULATOR"),

    SETTING("SETTING"),

    CAMERA("CAMERA"),
    /** 地图 */
    MAPS("MAPS"),
    /** 市场 */
    MARKET("MARKET"),
    /** 升级 */
    UPDATER("UPDATER"),

    MUSIC("MUSIC");

    private String value;

    public String getValue() {
        return value;
    }

    AppType(String type) {
        this.value = type;
    }
}
