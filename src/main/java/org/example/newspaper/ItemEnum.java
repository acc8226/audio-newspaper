package org.example.newspaper;

public enum ItemEnum {
    FM_367750("367750","小爱早报"),
    FM_456498("456498","小爱晚报"),
    FM_193187("193187","资讯一点通"),
    FM_1898397("1898397","凤凰资讯"),
    FM_1897566("1897566","资讯快报"),
    FM_636494("636494","国际快报"),
    FM_1898253("1898253","人本生活资讯早报"),
    FM_216504("216504","晚间新闻速递"),
    FM_216503("216503","凤凰早报"),
    FM_192803("192803","科技一点通"),
    FM_157239("157239","商业情报局"),
    FM_88040("88040","调皮电商"),
    FM_1898262("1898262","人本生活资讯晚报");

    private String pid;
    private String name;

    ItemEnum(String pid, String name) {
        this.pid = pid;
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }
}
