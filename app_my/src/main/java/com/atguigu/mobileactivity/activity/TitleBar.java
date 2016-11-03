package com.atguigu.mobileactivity.activity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileactivity.R;

/**
 * Created by 颜银 on 2016/9/28.
 * QQ:443098360
 * 微信：y443098360
 */
public class TitleBar extends LinearLayout {

    private TextView tv_title_search;
    private RelativeLayout rl_game;
    private ImageView iv_history;

    private Context context;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 当布局加载完成时回调此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_title_search = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_history = (ImageView) getChildAt(3);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        //设置点击监听
        tv_title_search.setOnClickListener(myOnClickListener);
        rl_game.setOnClickListener(myOnClickListener);
        iv_history.setOnClickListener(myOnClickListener);

    }
    class  MyOnClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_title_search :
//                    Toast.makeText(getContext(), "搜索", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,SearchActivity.class);
                    context.startActivity(intent);

                    break;
                case R.id.rl_game :
                    Toast.makeText(getContext(), "游戏", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_history :
                    Toast.makeText(getContext(), "历史记录", Toast.LENGTH_SHORT).show();
                    break;
            }
            
        }
    }
    
}
