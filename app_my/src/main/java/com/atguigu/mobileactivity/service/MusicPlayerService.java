package com.atguigu.mobileactivity.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.atguigu.mobileactivity.IMusicPlayerService;
import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.activity.AudioPlayerActivity;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.atguigu.mobileactivity.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by 颜银 on 2016/10/8.
 * QQ:443098360
 * 微信：y443098360
 * 作用：播放音乐的服务
 * 封装音乐的逻辑
 */
public class MusicPlayerService extends Service {

    //发广播的action常亮
    public static final String OPENAUDIO = "com.atguigu.mobilepalyer.OPENAUDIO";

    /**
     * 音频列表
     */
    private ArrayList<MedioItem> mediaItems;
    /**
     * 这个音频的信息
     */
    private MedioItem medioItem;
    private NotificationManager manager;


    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;//得到外面的实例

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);//调用服务的openAudio
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {
            service.setPlaymode(playmode);
        }

        @Override
        public int getPlaymode() throws RemoteException {
            return service.getPlaymode();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void notifyChange(String action) throws RemoteException {
            service.notifyChange(action);
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return medioItem.getData()  ;
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };
    /**
     * 当前播放的列表中的位置
     */
    private int position;
    private MediaPlayer mediaPlayer;
    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 0;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 1;
    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;
    /**
     * 播放模式
     */
    public int playmode = REPEAT_NORMAL;


    @Override
    public void onCreate() {
        super.onCreate();
        playmode = CacheUtils.getPlaymode(this, "playmode");//从内存中取出播放模式
        getData();
    }


    private void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//在sdcard时候的名称
                        MediaStore.Audio.Media.DURATION,//音频的时长，毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小，单位字节
                        MediaStore.Audio.Media.ARTIST,//演唱者
                        MediaStore.Audio.Media.DATA//在sdcard上路径
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    //循环
                    while (cursor.moveToNext()) {
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
                        medioItem.setData(data);//sd卡路径
                    }
                    //关闭cursor
                    cursor.close();
                }
                //发送消息
//                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    //千万不能忘记
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * 根据位置播放对应的音频文件
     *
     * @param position
     */
    private void openAudio(int position) {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (position < mediaItems.size()) {
                this.position = position;
                medioItem = mediaItems.get(position);
            }

            //释放MediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            try {
                //创建MediaPlayer，重新设置监听
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                //设置播放地址
                mediaPlayer.setDataSource(medioItem.getData());

                mediaPlayer.prepareAsync();//准备
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(MusicPlayerService.this, "音频还没有加完成", Toast.LENGTH_SHORT).show();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完成的时候播放下一个
//            next();
            autonext();//根据模式的不同来设置不同的下一个播放模式
        }
    }


    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //开始播放
            start();
//            notifyChange(OPENAUDIO);
            //4.发布事件(EventBus)
            EventBus.getDefault().post(medioItem);
        }
    }

    /**
     * 发广播
     *
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 播放音乐
     */

    private void start() {
        mediaPlayer.start();
        //开始播放音乐的时候弹出通知栏
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //延期的意图，放在下拉框中
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification", true);//从状态通知栏进入播放音乐的界面
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321影音")
                .setContentText("正在播放:" + getAudioName())
                .setContentIntent(pending)
                .build();//最低版本要求16
        notification.flags = Notification.FLAG_ONGOING_EVENT;//点击的时候不会消失
        manager.notify(1, notification);//通过相同的id来控制通知栏的显示  消失
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        mediaPlayer.pause();
        //暂停音乐隐藏通知栏
        manager.cancel(1);
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    private void setPlaymode(int playmode) {
        this.playmode = playmode;
        //保存模式
        CacheUtils.savePlaymode(this, "playmode", playmode);
    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlaymode() {
        return playmode;
    }

    /**
     * 得到艺术家
     *
     * @return
     */
    private String getArtist() {
        if (medioItem != null) {
            return medioItem.getArtist();
        }
        return "";
    }

    /**
     * 得到歌曲名称
     *
     * @return
     */
    private String getAudioName() {
        if (medioItem != null) {
            return medioItem.getName();
        }
        return "";
    }

    /**
     * 得到音频的总时长
     *
     * @return
     */
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 音频的拖动
     *
     * @param position
     */
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    /**
     * 播放下一个
     * 根据不同的播放模式播放下一个
     */
    private void next() {
        //设置播放的位置
        setNextPosition();
        //根据位置播放对应的音频
        openNextByPosition();
    }

    private void autonext() {
        //设置播放的位置
        setNextAutoPosition();
        //根据位置播放对应的音频
        openNextByPosition();
    }

    private void setNextAutoPosition() {
        //根据模式自动跳转歌曲
        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position++;//顺序播放，最后一首不会循环
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            //单曲循环，不用设设置，不会跳动
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            //循环播放，最后一个时，从第一个开始
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else {
            position++;//顺序播放，最后一首不会循环
        }
    }


    private void openNextByPosition() {
        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position < mediaItems.size()) {
                //正常范围正常开启下一首音乐
                openAudio(position);
            } else {
                //不正常 超出范围
                position = mediaItems.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position < mediaItems.size()) {
                //正常范围正常开启下一首音乐
                openAudio(position);
            } else {
                //不正常 超出范围
                position = mediaItems.size() - 1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else {
            position++;
        }
    }

    /**
     * 播放上一个
     */
    private void pre() {
//        setPrePosition();
        openPreByPosition();
    }

    private void openPreByPosition() {
        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position--;
            if (position >= 0) {
                //正常范围
                openAudio(position);
            } else {
                //超出范围
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE || playmode == MusicPlayerService.REPEAT_ALL) {
            position--;
            if (position < 0) {
                position = mediaItems.size() - 1;
            }
            openAudio(position);
        } else {
            position--;
            if (position >= 0) {
                //正常范围
                openAudio(position);
            } else {
                //超出范围
                position = 0;
            }
        }
    }

//    private void setPrePosition() {
//        int playmode = getPlaymode();
//        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
//            position--;
//        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
//            position--;
//            if (position < 0) {
//                position = mediaItems.size() - 1;
//            }
//        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
//            position--;
//            if (position < 0) {
//                position = mediaItems.size() - 1;
//            }
//        } else {
//            position--;
//        }
//    }
}
