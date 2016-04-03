package com.dh.home;

/* v3.1 */
public enum FolderType {

    // 聊天通讯
    ChatCommunication("ChatCommunication"),
    // 生活娱乐
    LifeAmusement("LifeAmusement"),
    // 影音播放
    AudioVideo("AudioVideo"),
    // 新闻阅读
    NewsReading("NewsReading"),
    // 游戏
    Game("Game"),
    // 学习办公
    LearnOffice("LearnOffice"),
    // 主题壁纸
    Theme("Theme"),
    // 系统工具
    SystemTools("SystemTools");

    private String value;

    public String getValue() {
        return value;
    }

    FolderType(String type) {
        this.value = type;
    }
}
