package com.atguigu.mobileactivity.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.activity.ShowImageAndGifActivity;
import com.atguigu.mobileactivity.adapter.NetAudioFragmentAdapter;
import com.atguigu.mobileactivity.domain.BaiSiBean;
import com.atguigu.mobileactivity.fragmentbase.BaseFragment;
import com.atguigu.mobileactivity.utils.Constants;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by 颜银 on 2016/9/28.
 * QQ:443098360
 * 微信：y443098360
 */
public class NetAudioFragment extends BaseFragment {
    //    private TextView textView;


    private ListView lv_audio;
    private ProgressBar progressbar;
    private TextView tv_nodata;

    private List<BaiSiBean.ListBean> list;
    private NetAudioFragmentAdapter adapter;
    private MaterialRefreshLayout refresh;

    @Override
    public View initView() {
        LogUtil.e("网络音乐UI创建了");
//        textView = new TextView(context);
//        textView.setTextSize(25);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);
        View view = View.inflate(context, R.layout.fragment_net_audio, null);
        lv_audio = (ListView) view.findViewById(R.id.lv_audio);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);

        //设置点击事件
        lv_audio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaiSiBean.ListBean listEntity = list.get(position);
                if(listEntity !=null ){
                    //3.传递视频列表
                    Intent intent = new Intent(context,ShowImageAndGifActivity.class);
                    if(listEntity.getType().equals("gif")){
                        String url = listEntity.getGif().getImages().get(0);
                        LogUtil.e("-=-=-=-"+ url);
                        intent.putExtra("url",url);
                        context.startActivity(intent);
                    }else if(listEntity.getType().equals("image")){
                        String url = listEntity.getImage().getBig().get(0);
                        LogUtil.e("-=-=-=-image"+ url);
                        intent.putExtra("url",url);

                        context.startActivity(intent);
                    }
                }

            }
        });

        //设置下拉刷新和加载更多的监听
        refresh.setMaterialRefreshListener(new MyMaterialRefreshListener());

        return view;
    }

    /**
     * 设置下拉刷新和加载更多的回调
     */
    class MyMaterialRefreshListener extends MaterialRefreshListener {

        /**
         * 下拉刷新
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //下拉刷新就是重新调用数据更新网络数据
            getDataFromNet();
        }

        /**
         * 上啦加载更多
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            getMoreDataFromNet();
        }
    }

    public void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.BAI_SI_BU_DE_JIE);
        x.http().get(params, new Callback.CommonCallback<String>() {

            /**
             * 当请求成功的时候回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                //数据缓存
//                CacheUtils.putString(context, Constants.BAI_SI_BU_DE_JIE, result);
                //解析数据
//                processData(result);
                parsedMoerJson(result);
                refresh.finishRefreshLoadMore();
            }

            /**
             *请求失败的时候回调
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex);

            }

            /**
             * 当请求取消了的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            /**
             * 请求完成的时候
             */
            @Override
            public void onFinished() {
                LogUtil.e("onFinished==请求完成的时候");

            }
        });
    }

    private void parsedMoerJson(String json) {
        //把数据添加到原来的集合中去,在解析一边添加进去
        list.addAll(parsedJson(json));//添加到原来的集合
        adapter.setData(list);//重新把数据设置到适配器
        adapter.notifyDataSetChanged();//刷新

    }


    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音乐数据绑定了");
//        textView.setText("网络音乐");
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.BAI_SI_BU_DE_JIE);
        x.http().get(params, new Callback.CommonCallback<String>() {

            /**
             * 当请求成功的时候回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                //数据缓存
//                CacheUtils.putString(context, Constants.BAI_SI_BU_DE_JIE, result);
                //解析数据
                list = parsedJson(result);
                if(list!= null&&list.size()>0){
                    //有视频
                    tv_nodata.setVisibility(View.GONE);
                    adapter = new NetAudioFragmentAdapter(context, list);
                    lv_audio.setAdapter(adapter);

                    //显示数据隐藏progressbar
                    progressbar.setVisibility(View.GONE);
                }else{
                    //没有视频
                    tv_nodata.setVisibility(View.VISIBLE);
                }
                //把下拉刷新的状态还原
                refresh.finishRefresh();
            }

            /**
             *请求失败的时候回调
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex);

            }

            /**
             * 当请求取消了的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            /**
             * 请求完成的时候
             */
            @Override
            public void onFinished() {
                LogUtil.e("onFinished==请求完成的时候");

            }
        });
    }

    /**
     * 解析json数据和显示数据
     *
     * @param json
     */
    private List<BaiSiBean.ListBean> parsedJson(String json) {
        Gson gson = new Gson();
        BaiSiBean baiSiBean = gson.fromJson(json, BaiSiBean.class);
        List<BaiSiBean.ListBean> baiSiBeans = baiSiBean.getList();
        LogUtil.e("baiSiBeans = " + baiSiBeans.toString());
        return baiSiBeans;
    }
}
