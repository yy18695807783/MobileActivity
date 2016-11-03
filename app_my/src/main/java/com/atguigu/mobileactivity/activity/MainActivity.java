package com.atguigu.mobileactivity.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.fragmentbase.BaseFragment;
import com.atguigu.mobileactivity.fragments.AudioFragment;
import com.atguigu.mobileactivity.fragments.NetAudioFragment;
import com.atguigu.mobileactivity.fragments.NetVideoFragment;
import com.atguigu.mobileactivity.fragments.RecyclerViewFragment;
import com.atguigu.mobileactivity.fragments.VideoFragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;

    /**
     *  * 标识是否退出
     *
     */
    private boolean mExitFlag = false;
    /**
     *  * 退出软件消息
     *
     */
    private Handler handler = new Handler();
    /**
     * 各个Fragemnt页面的集合
     */
    private ArrayList<BaseFragment> fragments;

    /**
     * 列表中对应的Fragment的位置
     */
    private int position;
    /**
     * 上一个页面的Fragment
     */
    private Fragment content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);

        //初始化Fragment
        initFragment();

        //设置RadioGroup状态改变的监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        rg_main.check(R.id.rb_video);
    }


    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_video:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_netvideo:
                    position = 2;
                    break;
                case R.id.rb_netaudio:
                    position = 3;
                    break;
                case R.id.rb_recycler:
                    position = 4;
                    break;
            }
            Fragment toFragment = getFragment(position);
            switchFragment(content, toFragment);
        }


    }

    /**
     * @param fromFragment 在点击这个时刻正在显示
     * @param toFragment   在点击后这个时刻马上要显示
     */
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {

        if (toFragment != content) {//才去显示
            if (toFragment != null) {
                content = toFragment;//videoFragment
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (!toFragment.isAdded()) {
                    //隐藏之前显示 fromFragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    //添加toFragment
                    transaction.add(R.id.fl_main_content, toFragment).commit();
//                    transaction.commit();
                } else {
                    // //隐藏之前显示 fromFragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    ///显示toFragment
                    transaction.show(toFragment).commit();

                }

            }

        }
//        Fragment fragment = getFragment(0);
//        //1.得到FragmentManger
//        android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
//        //2.开启事务
//        android.support.v4.app.FragmentTransaction transaction = supportFragmentManager.beginTransaction();
//        //3.替换
//        transaction.replace(R.id.fl_main_content,toFragment);
//        //4.提交事务
//        transaction.commit();
    }

    /**
     * 得到Fragment
     *
     * @param position
     * @return
     */
    private Fragment getFragment(int position) {
        return fragments.get(position);
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new VideoFragment());//0 本地视频
        fragments.add(new AudioFragment());//1 本地音频
        fragments.add(new NetVideoFragment());//2  网络视频
        fragments.add(new NetAudioFragment());//3  网络音频
        fragments.add(new RecyclerViewFragment());//3  RecyclerView

    }

    /**
     *   再按一次退出
     *
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (0 != position) {
                rg_main.check(R.id.rb_video);
                return true;
            } else if (!mExitFlag) {
                Toast.makeText(MainActivity.this, "2秒在按一次推出", Toast.LENGTH_SHORT).show();
                mExitFlag = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mExitFlag = false;
                    }
                }, 2000);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

}
