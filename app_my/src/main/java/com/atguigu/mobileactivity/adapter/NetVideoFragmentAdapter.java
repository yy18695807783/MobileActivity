package com.atguigu.mobileactivity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by 颜银 on 2016/10/7.
 * QQ:443098360
 * 微信：y443098360
 */
public class NetVideoFragmentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MedioItem> mediaItems;

    public NetVideoFragmentAdapter(Context context, ArrayList<MedioItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems == null ? 0 : mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video_fragment, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //装配数据
        MedioItem medioItem = mediaItems.get(position);
        //用xUtils3来装配图片集合数据
//        x.image().bind(viewHolder.iv_icon,medioItem.getImageUrl());
        //使用picasso开源框架来装配图片
//        Picasso.with(context)
//                .load(medioItem.getImageUrl())
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHolder.iv_icon);
        //使用glide开源框架来装配图片
        Glide.with(context)
                .load(medioItem.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.iv_icon);
        viewHolder.tv_name.setText(medioItem.getName());
        viewHolder.tv_desc.setText(medioItem.getDesc());
        viewHolder.tv_duration.setText(medioItem.getDuration() + "秒");

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        TextView tv_duration;
    }
}
