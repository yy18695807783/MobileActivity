package com.atguigu.mobileactivity.domain;

/**
 * Created by 颜银 on 2016/10/10.
 * QQ:443098360
 * 微信：y443098360
 * 作用： 歌词格式
 */
public class Lyric {

    private String content;//歌词内容
    private long timePoint;//歌词时间点
    private long sleepTime;//间隔时常  高亮时常

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
