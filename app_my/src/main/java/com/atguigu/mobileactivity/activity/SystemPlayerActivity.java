package com.atguigu.mobileactivity.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.atguigu.mobileactivity.utils.Utils;
import com.atguigu.mobileactivity.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 颜银 on 2016/9/28.
 * QQ:443098360
 * 微信：y443098360
 */
public class SystemPlayerActivity extends Activity implements View.OnClickListener {


    /**
     * 常量-视频进度更新
     */
    private static final int PROGRESS = 1;
    /**
     * 常量-隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLL = 2;
    /**
     * 默认
     */
    private static final int SCREEN_DEFULT = 1;
    /**
     * 全屏
     */
    private static final int SCREEN_FULL = 2;
    /**
     * 显示网速
     */
    private static final int SHOW_NET_SPEED = 3;

    /**
     * 调节屏幕亮度
     */
    private Vibrator vibrator;

    private LinearLayout ll_loading;
    private TextView tv_loading_speed;
    private TextView tv_buffer_speed;
    private LinearLayout ll_buffer;
    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVideoVoice;
    private SeekBar seekbarVoice;
    private Button btnVideoSwichPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrent;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwichScreen;
    private Utils utils;
    private MyBroadcastReceiver receiver;
    /**
     * 视频列表数据
     */
    private ArrayList<MedioItem> mediaItems;
    /**
     * 点击视频在列表中的位置
     */
    private int position;

    //1.定义手势识别器
    private GestureDetector detector;
    /**
     * 是否是全屏
     */
    private boolean isFullScreen = false;
    /**
     * 屏幕的宽和高
     */
    private int screenWidth;
    private int screenHeight;
    /**
     * 真实视频的宽和高
     */
    private int videoWidht;
    private int videoHeight;

    /**
     * 调节声音
     */
    private AudioManager am;
    /**
     * 当前音量
     */
    private int currentVolume;
    //最大音量
    private int maxVolume;
    /**
     * 是否网络地址
     */
    private boolean isNetUrl = false;
    /**
     * 上一次播放的位置
     */
    private int preCurrentPosition = 0;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-10-05 22:48:12 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_player);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVideoVoice = (Button) findViewById(R.id.btn_video_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnVideoSwichPlayer = (Button) findViewById(R.id.btn_video_swich_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrent = (TextView) findViewById(R.id.tv_current);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoExit = (Button) findViewById(R.id.btn_video_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwichScreen = (Button) findViewById(R.id.btn_video_swich_screen);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_buffer = (LinearLayout)findViewById(R.id.ll_buffer);
        tv_loading_speed = (TextView)findViewById(R.id.tv_loading_speed);
        tv_buffer_speed = (TextView)findViewById(R.id.tv_buffer_speed);

        btnVideoVoice.setOnClickListener(this);
        btnVideoSwichPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwichScreen.setOnClickListener(this);
        //设置最大音量
        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);

        //发消息更新网络速度
        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }


    private boolean isMute = false;

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-10-05 22:48:12 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVideoVoice) {
            isMute = !isMute;
            updataVolume(currentVolume);
            // Handle clicks for btnVideoVoice
        } else if (v == btnVideoSwichPlayer) {
            //播放器切换按钮
            showSwitchVideoPlayer();
            // Handle clicks for btnVideoSwichPlayer
        } else if (v == btnVideoExit) {
            // Handle clicks for btnVideoExit
            finish();
        } else if (v == btnVideoPre) {
            setPlayPreVideo();
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {//播放和暂停
            setStartAndPause();
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            setPlayNextVideo();
            // Handle clicks for btnVideoNext
        } else if (v == btnVideoSwichScreen) {
            //切换视频模式
            setVideoMode();
            // Handle clicks for btnVideoSwichScreen
        }
        handler.removeMessages(HIDE_MEDIACONTROLL);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
    }

    private void showSwitchVideoPlayer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("当前使用系统播放器播放视频，如果只有声音没有画面，请切换到万能播放器播放")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSystemPlayer();
                    }
                })
                .show();
    }

    private void startSystemPlayer() {
        if(videoview != null){
            videoview.stopPlayback();
        }
        //传递列表和单个视频
        //调起自己的播放器

        Intent intent = new Intent(this, SystemPlayerActivity.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
        //使用Bundler传递列表数据
        if (mediaItems != null && mediaItems.size() > 0) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("medialist", mediaItems);
            intent.putExtra("position", position);
            intent.putExtras(bundle);

        } else if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);

        //把当前页面关闭
        finish();
    }

    private void setVideoMode() {
        if (isFullScreen) {
            //默认
            setVideoType(SCREEN_DEFULT);
        } else {
            //全屏
            setVideoType(SCREEN_FULL);
        }
    }

    private void setVideoType(int videoType) {
        switch (videoType) {
            case SCREEN_FULL:
                isFullScreen = true;
                videoview.setVideoSize(screenWidth, screenHeight);

                //按钮状态
                btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_defualt_selector);

                break;
            case SCREEN_DEFULT:
                isFullScreen = false;


                //真实视频的宽和高
                int mVideoWidth = videoWidht;
                int mVideoHeight = videoHeight;

                //屏幕的真实宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoview.setVideoSize(width, height);

                //按钮状态
                btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_full_selector);

                break;
        }
    }

    private void setPlayNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {

                MedioItem mediaItem = mediaItems.get(position);
                isNetUrl = utils.isNetUrl(mediaItem.getData());
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                ll_loading.setVisibility(View.VISIBLE);
                setButtonState();
                if (position == mediaItems.size() - 1) {
                    Toast.makeText(SystemPlayerActivity.this, "播放最后一个视频", Toast.LENGTH_SHORT).show();
                }

            } else {
                position = mediaItems.size() - 1;
                //列表的播放完成
                finish();//退出播放器
            }
        } else if (uri != null) {//只有一个播放地址  mediaItems.size == 0
            finish();//退出播放器
        }
    }

    /**
     * 播放上一个视频
     */
    private void setPlayPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {

                MedioItem mediaItem = mediaItems.get(position);
                isNetUrl = utils.isNetUrl(mediaItem.getData());
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());
                ll_loading.setVisibility(View.VISIBLE);
                setButtonState();

            } else {
                //列表的播放完成
                position = 0;
            }
        }
    }

    /**
     * 设置控制面版按钮的方法(上一个视频和下一个视频)
     */
    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {

            //设置上一个和下一个可以点击
            setIsEnableButton(true);


            //如果是第0个，上一个不可以点
            if (position == 0) {
                Log.e("TAG", "setButtonState 上一个");
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
            }

            //如果是最后一个，下一个按钮不可以点
            if (position == mediaItems.size() - 1) {
                Log.e("TAG", "setButtonState 下一个");
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            }


        } else if (uri != null) {//只有一个播放地址
            Log.e("TAG", "setButtonState  url");
            setIsEnableButton(false);

        }
    }

    /**
     * 设置上一个  下一个视频播放的点击按钮状态
     *
     * @param enable
     */
    private void setIsEnableButton(boolean enable) {
        if (enable) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
        } else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        }

        btnVideoPre.setEnabled(enable);
        btnVideoNext.setEnabled(enable);
    }

    /**
     * 更新声音进度条(相当于设置改变音量大小)
     *
     * @param progress
     */
    private void updataVolume(int progress) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            currentVolume = progress;
            seekbarVoice.setProgress(progress);
        }
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SHOW_NET_SPEED:
                    String netSpeed = utils.showNetSpeed(SystemPlayerActivity.this);
                    tv_buffer_speed.setText(netSpeed);
                    tv_loading_speed.setText(netSpeed);
                    //循环发消息
                    removeMessages(SHOW_NET_SPEED);
                    sendEmptyMessageDelayed(SHOW_NET_SPEED, 1000);
                    break;
                case PROGRESS:
                    //1.得到当前进度
                    int currentPosition = videoview.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);

                    //设置时间跟新
                    tvCurrent.setText(utils.stringForTime(currentPosition));

                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());

                    //设置视频缓存效果
                    if (isNetUrl) {
                        //网络的
                        int buffer = videoview.getBufferPercentage();//返回0--100的值

                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        //设置视频的缓冲
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }


                    //自定义监听卡
                    if (isNetUrl) {
                        if (videoview.isPlaying()) {
                            //当前进度减掉上一次播放进度小于500就是卡
                            int buffer = currentPosition - preCurrentPosition;
                            if (buffer < 500) {//视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                ll_buffer.setVisibility(View.GONE);
                            }

                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }


                    preCurrentPosition = currentPosition;
                    //循环发消息
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
                case HIDE_MEDIACONTROLL://隐藏
                    hideMediaController();
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
        initData();
        findViews();

        getData();
        setListener();

        setData();

        //设置控制面板
//       videoview.setMediaController(new MediaController(this));

    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //有列表数据
            MedioItem mediaItem = mediaItems.get(position);
            isNetUrl = utils.isNetUrl(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            videoview.setVideoPath(mediaItem.getData());

        }
        //文件，第三方应用
        else if (uri != null) {
            //设置播放地址
            isNetUrl = utils.isNetUrl(uri.toString());
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
        } else {
            Toast.makeText(SystemPlayerActivity.this, "没有传递数据进入播放器", Toast.LENGTH_SHORT).show();
        }

        //设置按钮状态
        setButtonState();
    }

    private void getData() {
        uri = getIntent().getData();//获得播放地址URI
        LogUtil.e("Uri = " + uri);

        mediaItems = (ArrayList<MedioItem>) getIntent().getSerializableExtra("medialist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void initData() {
        //注册广播--电量变化的广播
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        //创建手势识别器
        detector = new GestureDetector(this, new MySimpleOnGestureListener());

        //得到屏幕的宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;

        //得到当前相关音量信息
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void setListener() {
        //设置视频播放的监听：准备好，播放出错，播放完成
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置视频拖动的监听
        seekbarVideo.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        //设置音量的拖动
        seekbarVoice.setOnSeekBarChangeListener(new VolumeOnSeekBarChangeListener());

        //使用系统的监听播放卡,在Android4.2.2（17）以上
//        if(Bundle.VERSION.SDK_INF >=Bundle.VERSION_CODES.JELLY_BEAN_MR1){
//            videoview.setOnInfoListener(new MyOnInfoListener());
//        }
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener{


        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                //视频卡了，拖拽卡
                case MediaPlayer.MEDIA_INFO_BUFFERING_START :
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                //视频卡结束，拖拽卡结束
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    }

    class VolumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                updataProgree(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            handler.removeMessages(HIDE_MEDIACONTROLL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
        }
    }

    private void updataProgree(int progress) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        currentVolume = progress;
        seekbarVoice.setProgress(progress);
        if (progress > 0) {
            isMute = false;
        } else {
            isMute = true;
        }
    }


    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当seekbar改变的监听
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //只响应用户操作
            if (fromUser) {

                videoview.seekTo(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //            finish();//退出播放器
//            mp.seekTo(0);
//            mp.start();
            setPlayNextVideo();
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(SystemPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
            //1.播放的视频格式不支持，播放出错--使用万能播放器播放
            startVitamioPlayer();
            //2.播放过程中网络中断，播放出错--重试
            //3.播放视频文件有缺损，播放出错
            return true;
        }
    }

    /**
     * 跳转到万能播放器
     */
    private void startVitamioPlayer() {

        if(videoview != null){
            videoview.stopPlayback();
        }
        //传递列表和单个视频
        //调起自己的播放器
        Intent intent = new Intent(SystemPlayerActivity.this,VitamioPlayerActivity.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
        //使用Bundler传递列表数据
        if(mediaItems != null && mediaItems.size() > 0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("medialist", mediaItems);
            intent.putExtra("position", position);
            intent.putExtras(bundle);
        }else{
            intent.setData(uri);
        }
        startActivity(intent);

        //把当前页面关闭
        finish();
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {


//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemPlayerActivity.this, "视频拖动完成了", Toast.LENGTH_SHORT).show();
//                }
//            });

            //得到视频的宽和高
            videoWidht = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

//            mp.setLooping(true);


            //开始播放
            videoview.start();

            //得到视频的总时长
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);

            tvDuration.setText(utils.stringForTime(duration));

            //默认隐藏控制面板
            hideMediaController();
//            mp.start();
            handler.sendEmptyMessage(PROGRESS);

            setVideoType(SCREEN_DEFULT);
//            videoview.setVideoSize(50,50);

            //隐藏视频加载效果
            ll_loading.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        //先把子类释放
        handler.removeCallbacksAndMessages(null);//把所有的消息和回调移除
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        //最后释放父类调用父类方法，否则可能回空指针
        super.onDestroy();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra("level", 0);//0~100
            //主线程
            //显示电量
            setBattery(level);

        }

        /**
         * 显示电量
         *
         * @param level
         */
        public void setBattery(int level) {
            if (level <= 0) {
                ivBattery.setImageResource(R.drawable.ic_battery_0);
            } else if (level <= 10) {
                ivBattery.setImageResource(R.drawable.ic_battery_10);
            } else if (level <= 20) {
                ivBattery.setImageResource(R.drawable.ic_battery_20);
            } else if (level <= 40) {
                ivBattery.setImageResource(R.drawable.ic_battery_40);
            } else if (level <= 60) {
                ivBattery.setImageResource(R.drawable.ic_battery_60);
            } else if (level <= 80) {
                ivBattery.setImageResource(R.drawable.ic_battery_80);
            } else if (level <= 100) {
                ivBattery.setImageResource(R.drawable.ic_battery_100);
            } else {
                ivBattery.setImageResource(R.drawable.ic_battery_100);
            }
        }
    }

    private boolean isShowMediaController = false;

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {//长按暂停/开始
//            Toast.makeText(SystemPlayerActivity.this, "我被长按了", Toast.LENGTH_SHORT).show();
            setStartAndPause();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {//双击设置全屏模式/解除全屏
//            Toast.makeText(SystemPlayerActivity.this, "我被双击了", Toast.LENGTH_SHORT).show();
            setVideoMode();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Toast.makeText(SystemPlayerActivity.this, "我被单击了", Toast.LENGTH_SHORT).show();
            if (isShowMediaController) {
                //隐藏
                hideMediaController();
                //把消息移除
                handler.removeMessages(HIDE_MEDIACONTROLL);
            } else {
                //显示
                showMediaController();
                //发延迟消息
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        isShowMediaController = false;
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        isShowMediaController = true;
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    private void setStartAndPause() {
        if (videoview.isPlaying()) {
            //暂停
            videoview.pause();
            //按钮状态--播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //播放
            videoview.start();
            //按钮-暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 当前音量
     */
    private int mVol;
    /**
     * 总距离
     */
    private int rangTouch;
    /**
     * 开始的位置
     */
    private float startY;
    /**
     * 用于区分屏幕宽度分成左右  调节音量--亮度
     */
    private float startX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //3.把事件传入手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Intent intent = new Intent(this,TestB.class);
//                startActivity(intent);
//                return true;
                //1.记录当前音量，滑动的起始位置，得到总距离，移除消息
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                rangTouch = Math.min(screenHeight, screenWidth);//screenHeight 横屏后高度小
                startY = event.getY();
                startX = event.getX();
                handler.removeMessages(HIDE_MEDIACONTROLL);
            case MotionEvent.ACTION_MOVE:
                //2.记录endY,计算偏离，计算改变声音，最终音量，修改声音
                float endY = event.getY();
                float endX = event.getX();

                if(endX< screenWidth / 2){
                    //左边调节屏幕亮度

                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (startY - endY > FLING_MIN_DISTANCE
                            && Math.abs(endY - startY) > FLING_MIN_VELOCITY) {
                        setBrightness(20);
                    }
                    if (startY - endY < FLING_MIN_DISTANCE
                            && Math.abs(endY - startY) > FLING_MIN_VELOCITY) {
                        setBrightness(-20);
                    }

                }else {
                    //右边调节声音大小

                    //滑动的距离：总距离  = 改变声音： 最大音量
                    float distance = startY - endY;
                    //改变声音 = （滑动的距离/总距离）*最大音量
                    float delta = (distance / rangTouch) * maxVolume;
                    //最终音量 = 原来音量 + 改变声音
                    int volume = (int) Math.min(Math.max(mVol + delta, 0), maxVolume);
                    if (delta != 0) {
                        //设置当前音量大小
                        updataVolume(volume);
                    }
                }

                //重新复制
//                startY = event.getY();//不要重新赋值，否则会出现滑动卡吨现象，不流畅
                break;
            case MotionEvent.ACTION_UP:
                //重新发送消息
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
                break;
        }
        return true;
    }

    /**
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    private void setBrightness(float  brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
        Log.e("TAG", "lp.screenBrightness= " + lp.screenBrightness);
        getWindow().setAttributes(lp);
    }
    /**
     * 物理键调试声音
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //声音增大的按键
            currentVolume--;
            updataVolume(currentVolume);
            showMediaController();//显示控制台
            handler.sendEmptyMessage(HIDE_MEDIACONTROLL);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
            //返回true，消费重写系统声音进度条，不显示系统声音进度条
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //声音减小的物理键
            currentVolume++;
            updataVolume(currentVolume);
            showMediaController();//显示控制台
            handler.sendEmptyMessage(HIDE_MEDIACONTROLL);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        if (null != vibrator) {
            vibrator.cancel();
        }
        super.onStop();
    }
}
