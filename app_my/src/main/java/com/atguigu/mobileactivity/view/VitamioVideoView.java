package com.atguigu.mobileactivity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by 颜银 on 2016/10/5.
 * QQ:443098360
 * 微信：y443098360
 */
public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {
    public VitamioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置视频的宽和高
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = videoWidth;
        l.height = videoHeight;
        setLayoutParams(l);
    }
}
