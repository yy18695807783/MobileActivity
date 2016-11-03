package com.atguigu.mobileactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.atguigu.mobileactivity.activity.MainActivity;

public class WelcomeActivity extends Activity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //主线程
                startMainActivity();
            }
        }, 2000);

    }

    private boolean isStart = false;

    private void startMainActivity() {
        if(!isStart){
            isStart = true;
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            //启动后销毁当前页面
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN :
//                startMainActivity();
//                break;
//        }
                startMainActivity();
        return super.onTouchEvent(event);
    }
        /**
     * Activity销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除退出还会加载MainActivity的bug
        handler.removeCallbacksAndMessages(null);
    }
}
