package com.atguigu.mobileactivity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.atguigu.mobileactivity.domain.Lyric;

import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;

/**
 * Created by 颜银 on 2016/10/10.
 * QQ:443098360
 * 微信：y443098360
 * 作用： 显示歌词 并显示同步控件
 */
public class ShowLyricView extends TextView {

    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    /**
     * 歌词列表中的索引
     */
    private int index;
    /**
     * 每行高
     */
    private float textHeight = 20;

    /**
     * 歌曲的播放进度  这一句花的时间
     */
    private float currentPosition;
    /**
     * 画笔
     */
    private Paint paint;
    private Paint whitepaint;
    /**
     * 时间戳
     * 10-->10.11
     */
    private float timePoint;
    /**
     * 某一句的高亮显示时间
     */
    private float sleepTime;

    /**
     * 在布局文件中使用，将会采用该方法实例化该类，如果不存在，会崩溃
     *
     * @param context
     * @param attrs
     */
    public ShowLyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    private void initView() {

        textHeight = DensityUtil.dip2px(20);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(20));
        paint.setTextAlign(Paint.Align.CENTER);//水平居中

        whitepaint = new Paint();
        whitepaint.setAntiAlias(true);
        whitepaint.setColor(Color.WHITE);
        whitepaint.setTextSize(DensityUtil.dip2px(18));
        whitepaint.setTextAlign(Paint.Align.CENTER);//水平居中

        //假设歌词
//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for(int i = 0; i < 1000;i++){
//            lyric.setContent(i + "HHHHHHHHHHHH" + i);
//            lyric.setSleepTime(1000);
//            lyric.setTimePoint(1000 * i);
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            float push = 0;
            if (sleepTime == 0) {
                push = 0;
            } else {
                // 这一句花的时间： 这一句休眠时间  =  这一句要移动的距离：总距离(行高)

                //这一句要移动的距离 = （这一句花的时间/这一句休眠时间） * 总距离(行高)
//                float delta =  ((currentPosition -timePoint)/sleepTime)*textHeight;

                //在屏幕上的坐标 =  总距离(行高) + 这一句要移动的距离
//                push = textHeight + ((currentPosition - timePoint) / sleepTime) * textHeight;//这个最后一句歌词会跳动
                push =((currentPosition - timePoint) / sleepTime) * textHeight;
            }
            canvas.translate(0, -push);
            //有歌词
            //绘制当前句歌词
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);


            //绘制上面部分
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitepaint);
            }


            //绘制下面部分
            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitepaint);
            }


        } else {
            //没有歌词
            canvas.drawText("没有找到歌词...", width / 2, height / 2, paint);

        }

    }

    /**
     * @param position 当前歌曲的播放进度
     */
    public void setNextShowLyric(int position) {//position是传进来进度条的currentPosition
        this.currentPosition = position;
        //根据当前歌曲的播放进度，找到歌词列表的索引
        //重新绘制
        if (lyrics == null || lyrics.size() == 0)
            return;

        //不为空
//        for (int i = 1; i < lyrics.size(); i++) {
//            if (currentPosition < lyrics.get(i).getTimePoint()) {
//                int temtIndex = i - 1;//0    上半部分往上移动
//                if (currentPosition >= lyrics.get(temtIndex).getTimePoint()) {
//                    //0；
//                    index = temtIndex;
//                    //缓缓往上推移
//                    //时间戳
//                    timePoint = lyrics.get(index).getTimePoint();
//                    //高亮显示时间
//                    sleepTime = lyrics.get(index).getSleepTime();
//
//                }
//            }
//        }


        for (int i = 0; i < lyrics.size(); i++) {
            if (i + 1 < lyrics.size()) {
                if (currentPosition < lyrics.get(i + 1).getTimePoint() && currentPosition >= lyrics.get(i).getTimePoint()) {
                    index = i;
                }
            } else if (i + 1 == lyrics.size()) {
                if (currentPosition >= lyrics.get(i).getTimePoint()) {
                    index = i;
                }
            }
            //缓缓往上推移
            timePoint = lyrics.get(index).getTimePoint();
            sleepTime = lyrics.get(index).getSleepTime();
        }


        invalidate();//回调当作onDraw执行

    }

    public void setLyric(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

}
