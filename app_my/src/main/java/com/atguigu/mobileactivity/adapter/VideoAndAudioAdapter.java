package com.atguigu.mobileactivity.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.atguigu.mobileactivity.utils.Utils;

import java.util.ArrayList;

/**
 * Created by 颜银 on 2016/10/8.
 * QQ:443098360
 * 微信：y443098360
 * 作用：
 */
public class VideoAndAudioAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MedioItem> mediaItems;
    private Utils utils;
    private boolean isVideo;

    public VideoAndAudioAdapter(Context context,ArrayList<MedioItem> mediaItems,boolean isVideo){
        this.context = context;
        this.mediaItems = mediaItems;
        this.isVideo = isVideo;
        utils = new Utils();

    }
    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_video_fragment,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_video_icon = (ImageView) convertView.findViewById(R.id.iv_video_icon);
            viewHolder.tv_video_name = (TextView) convertView.findViewById(R.id.tv_video_name);
            viewHolder.tv_video_duration = (TextView) convertView.findViewById(R.id.tv_video_duration);
            viewHolder.tv_video_size = (TextView) convertView.findViewById(R.id.tv_video_size);

            //设置Tag
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!isVideo){
            viewHolder.iv_video_icon.setImageResource(R.drawable.music_default_bg);
        }
        //适配数据
        MedioItem medioItem = mediaItems.get(position);
        viewHolder.tv_video_name.setText(medioItem.getName());
        viewHolder.tv_video_duration.setText(utils.stringForTime((int) medioItem.getDuration()));
        viewHolder.tv_video_size.setText(Formatter.formatFileSize(context, medioItem.getSize()));

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_video_icon;
        TextView tv_video_name;
        TextView tv_video_duration;
        TextView tv_video_size;
    }
}
