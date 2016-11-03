package com.atguigu.mobileactivity.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileactivity.IMusicPlayerService;
import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.domain.Lyric;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.atguigu.mobileactivity.service.MusicPlayerService;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.atguigu.mobileactivity.utils.LyricUtils;
import com.atguigu.mobileactivity.utils.Utils;
import com.atguigu.mobileactivity.view.BaseVisualizerView;
import com.atguigu.mobileactivity.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    private static final int SHOWLYRIC = 2;
    private ImageView iv_icon;
    private int position;
    /**
     * 服务的代理类-aidl文件动态生成的类
     */
    private IMusicPlayerService service;
    private MyBroadcastReceiver receiver;
    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 当和服务连接成功的时候回调
         * @param name
         * @param binder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = IMusicPlayerService.Stub.asInterface(binder);//得到服务的代理类
            LogUtil.e("onServiceConnected" + service.toString());
            //开始播放音乐
            try {
                if (notification) {//是通知栏
                    //状态栏
                    Log.e("TAG11", "onServiceConnected----");
                    showProgress();//更新当前进度
                    showLyric();//显示歌词
                    //开启频谱
//                    setupVisualizerFxAndUi();
                    service.notifyChange(MusicPlayerService.OPENAUDIO);//发广播
                } else {
                    //列表
                    service.openAudio(position);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            //得到歌曲名称和演唱者名称并且显示
            showData();

        }

        /**
         * 当断开服务的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void showData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getAudioName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Visualizer mVisualizer;

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {

        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        System.out.println("audioSessionid==" + audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        baseVisualizerView.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }

    /**
     * 3.订阅函数
     * EventBus
     * @param medioItam
     */
    //必须加@Subscribe  参数最少有第一个  主线程？
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)//参数  线程  XXX   优先级
    public void setData(MedioItem medioItam){
        //得到演唱者和歌名并显示
        tvArtist.setText(medioItam.getArtist());
        tvName.setText(medioItam.getName());
//        showData();
        //歌曲开始更新
        showProgress();

        //校验内存中的播放模式
        checkPlaymode();
        //显示歌词
        showLyric();
        //开启频谱
        setupVisualizerFxAndUi();
    }

    private void showLyric() {
        //1.得到音频的播放地址
        LyricUtils lyricUtils = new LyricUtils();
        try {
            String path =service.getAudioPath();//mnt/sdcard/audio/beijingbeijing.mp3
            path = path.substring(0,path.lastIndexOf("."));//mnt/sdcard/audio/beijingbeijing

            //组合成新歌词文件
            File file = new File(path + ".lrc");//mnt/sdcard/audio/beijingbeijing.lrc
            if(!file.exists()){
                file = new File(path + ".txt");//mnt/sdcard/audio/beijingbeijing.txt
            }

            //解析歌词
            lyricUtils.readLyricFile(file);

            ArrayList<Lyric> lyrics = lyricUtils.getLyrics();
            show_lyric_view.setLyric(lyrics);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //2.变成歌词文件的地址
        if(lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOWLYRIC);
        }
    }

    /**
     * 进度更新
     */
    private static final int PROGERSS = 1;
    private TextView tvDuration;
    private SeekBar seekbarAudio;
    private TextView tvArtist;
    private TextView tvName;
    private Button btnAudioPlaymode;
    private ShowLyricView show_lyric_view;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnAudioSwichLyricCover;
    private Utils utils;
    private BaseVisualizerView baseVisualizerView;

    //是否来自通知栏---true,来自状态栏，false:列表
    private boolean notification;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-10-09 00:50:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnAudioSwichLyricCover = (Button) findViewById(R.id.btn_audio_swich_lyric_cover);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);

        baseVisualizerView = (BaseVisualizerView)findViewById(R.id.baseVisualizerView);

        show_lyric_view = (ShowLyricView)findViewById(R.id.show_lyric_view);
        iv_icon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getBackground();
        animationDrawable.start();

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioSwichLyricCover.setOnClickListener(this);

        //设置音频进度的拖拽
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 进度条改变的监听
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-10-09 00:50:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            changePlaymode();
            // Handle clicks for btnAudioPlaymode
        } else if (v == btnAudioPre) {
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnAudioPre
        } else if (v == btnAudioStartPause) {
            startAndPause();
            // Handle clicks for btnAudioStartPause
        } else if (v == btnAudioNext) {
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnAudioNext
        } else if (v == btnAudioSwichLyricCover) {
            // Handle clicks for btnAudioSwichLyricCover
        }
    }

    private void changePlaymode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保存模式--内存中
            service.setPlaymode(playmode);

            //显示播放模式
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示播放模式
     */
    private void showPlaymode() {

        //从内存中获取播放模式
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验显示播放模式
     */
    private void checkPlaymode() {
        try {
            //从内存中获取播放模式
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startAndPause() {
        try {
            if (service.isPlaying()) {
                //暂停
                service.pause();
                //按钮设置-播放状态
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            } else {
                //播放
                service.start();
                //按钮设置-暂停
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOWLYRIC:
                    //得到当前播放进度

                    try {
                        int position = service.getCurrentPosition();
                        //根据当前歌曲的播放进度，找到歌词列表的索引
                        //重新绘制  show_lyric_view 是ShowLyricView的实例
                        show_lyric_view.setNextShowLyric(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOWLYRIC);//一直发消息更新
                    sendEmptyMessage(SHOWLYRIC);
                    break;
                case PROGERSS:
                    int currentPosition = 0;
                    try {
                        currentPosition = service.getCurrentPosition();
                        //更新时间进度
                        tvDuration.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    seekbarAudio.setProgress(currentPosition);


                    removeMessages(PROGERSS);
                    sendEmptyMessageDelayed(PROGERSS, 1000);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        startAndBindService();
    }

    private void initData() {
        //注册广播
        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);
        utils = new Utils();

        //1.注册-this是AudioPlayerActivity
        EventBus.getDefault().register(this);


    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //得到歌曲名称和演唱者名称并且显示,初始化时能更新上一首及下一首歌曲的名称作者信息
            showData();
            //歌曲开始更新
            showProgress();

            Log.e("TAG11", "MyBroadcastReceiver----");
            //校验sp存储中的音乐播放模式
            checkPlaymode();
        }


    }

    private void showProgress() {
        try {
            seekbarAudio.setMax(service.getDuration());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(PROGERSS);
    }


    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.atguigu.mobileplayer.OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);//参数3：一旦绑定，就是创建service
        startService(intent);//防止服务多次实例化,star启动只会有一个service实例
    }

    public void getData() {
        //true,来自状态栏，false:列表
        notification = getIntent().getBooleanExtra("notification", false);//列表

        if (!notification) {
            position = getIntent().getIntExtra("position", 0);//列表
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (isFinishing()) {
//            mVisualizer.release();
//        }
    }
    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (conn != null) {
            unbindService(conn);
            conn = null;
            LogUtil.e("onDestroy    unbindService(conn)  解绑服务" );
        }

        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        //2.EventBus接注册
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
