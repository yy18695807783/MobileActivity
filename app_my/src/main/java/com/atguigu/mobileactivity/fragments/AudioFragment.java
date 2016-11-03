package com.atguigu.mobileactivity.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.activity.AudioPlayerActivity;
import com.atguigu.mobileactivity.adapter.VideoAndAudioAdapter;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.atguigu.mobileactivity.fragmentbase.BaseFragment;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.atguigu.mobileactivity.utils.Utils;

import java.util.ArrayList;

/**
 * Created by 颜银 on 2016/9/28.
 * QQ:443098360
 * 微信：y443098360
 */
public class AudioFragment extends BaseFragment {

    private TextView tv_nomedia;

    private ListView listview;
    /**
     * 音频列表
     */
    private ArrayList<MedioItem> mediaItems;

    private Utils utils;
    private VideoAndAudioAdapter adapter;

//    private TextView textView;


    public AudioFragment() {
        utils = new Utils();
    }

    /**
     * 初始化视图
     * @return
     */
    @Override
    public View initView() {
        LogUtil.e("本地音频UI创建了");
//        textView = new TextView(context);
//        textView.setTextSize(25);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);
        View view = View.inflate(context, R.layout.video_fragment,null);
        listview = (ListView) view.findViewById(R.id.listview);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);

        //设置点击监听
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MedioItem medioItem = mediaItems.get(position);
//            Toast.makeText(context, "medioItem==" + medioItem, Toast.LENGTH_SHORT).show();

            //把系统的播放器调起来并且播放
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(medioItem.getData()),"video/*");
//            context.startActivity(intent);
            //调起自己的播放器
            Intent intent = new Intent(context,AudioPlayerActivity.class);
//            intent.setDataAndType(Uri.parse(medioItem.getData()),"video/*");

//            Bundle bundle = new Bundle();
//            bundle.putSerializable("medialist", mediaItems);
            intent.putExtra("position", position);
//            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地音频数据绑定了");

        getData();
    }

    private void getData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//在sdcard时候的名称
                        MediaStore.Audio.Media.DURATION,//音频的时长，毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小，单位字节
                        MediaStore.Audio.Media.ARTIST,//演唱者
                        MediaStore.Audio.Media.DATA//在sdcard上路径
                };
                Cursor cursor = resolver.query(uri,objs,null,null,null);
                if(cursor != null){
                    //循环
                    while (cursor.moveToNext()){
                        //创建一个音频信息类
                        MedioItem medioItem = new MedioItem();
                        //添加到集合中
                        mediaItems.add(medioItem);


                        String name = cursor.getString(0);
                        medioItem.setName(name);
                        long duration = cursor.getLong(1);
                        medioItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        medioItem.setSize(size);
                        String artist = cursor.getString(3);
                        medioItem.setArtist(artist);
                        String data = cursor.getString(4);
                        medioItem.setData(data);
                    }
                    //关闭cursor
                    cursor.close();
                }
                //发送消息
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItems != null &&mediaItems.size() > 0){
                //有数据
                tv_nomedia.setVisibility(View.GONE);
                //适配器
                adapter = new VideoAndAudioAdapter(context,mediaItems,false);
                listview.setAdapter(adapter);
            }else {
                //没有数据
                tv_nomedia.setVisibility(View.VISIBLE);
            }
        }
    };


//    /**
//     * 解决安卓6.0以上版本不能读取外部存储权限的问题
//     * @param activity
//     * @return
//     */
//    public static boolean isGrantExternalRW(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            activity.requestPermissions(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            }, 1);
//
//            return false;
//        }
//
//        return true;
//    }
}
