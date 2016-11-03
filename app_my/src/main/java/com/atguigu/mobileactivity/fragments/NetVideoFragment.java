package com.atguigu.mobileactivity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.activity.SystemPlayerActivity;
import com.atguigu.mobileactivity.adapter.NetVideoFragmentAdapter;
import com.atguigu.mobileactivity.domain.MedioItem;
import com.atguigu.mobileactivity.fragmentbase.BaseFragment;
import com.atguigu.mobileactivity.utils.CacheUtils;
import com.atguigu.mobileactivity.utils.Constants;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by 颜银 on 2016/9/28.
 * QQ:443098360
 * 微信：y443098360
 */
public class NetVideoFragment extends BaseFragment {
    //    private TextView textView;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tv_nodata;
    private NetVideoFragmentAdapter adapter;
    private MaterialRefreshLayout refresh;
    /**
     * 视频列表
     */
    private ArrayList<MedioItem> mediaItems;

    @Override
    public View initView() {
        LogUtil.e("网络视频UI创建了");
//        textView = new TextView(context);
//        textView.setTextSize(25);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.RED);
        View view = View.inflate(context, R.layout.fragment_netvideo, null);
        listview = (ListView) view.findViewById(R.id.listview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);

        //设置点击监听
        listview.setOnItemClickListener(new MyOnItemClickListener());

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
         * @param materialRefreshLayout
         */
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //下拉刷新就是重新调用数据更新网络数据
            getDataFromNet();
        }

        /**
         * 上啦加载更多
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
//使用xUtils3联网
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URL);
//        RequestParams params = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 当请求成功的时候回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);

                //解析数据
                processMoreData(result);
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

    private void processMoreData(String json) {
        //把数据添加到原来的集合中去,在解析一边添加进去
        mediaItems.addAll(parsedJson(json));
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
        //把加载更多的状态还原
        refresh.finishRefreshLoadMore();
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //调起自己的播放器
            Intent intent = new Intent(context, SystemPlayerActivity.class);
            //            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");

            //使用Bundler传递列表数据
            Bundle bundle = new Bundle();
            bundle.putSerializable("medialist", mediaItems);
            intent.putExtra("position", position);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频数据绑定了");
//        textView.setText("网络视频内容");
        //取出缓存数据
        String saveJson = CacheUtils.getString(context,Constants.NET_VIDEO_URL);
//        if(!TextUtils.isEmpty(saveJson)){
//            processData(saveJson);
//        }
        if(!"".equals(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {
        //使用xUtils3联网
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URL);
//        RequestParams params = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 当请求成功的时候回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                //数据缓存
                CacheUtils.putString(context, Constants.NET_VIDEO_URL,result);
                //解析数据
                processData(result);
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
    private void processData(String json) {
        mediaItems = parsedJson(json);
        LogUtil.e(mediaItems.toString());
        if (mediaItems != null && mediaItems.size() > 0) {
            //有数据
            adapter = new NetVideoFragmentAdapter(context,mediaItems);
            listview.setAdapter(adapter);
        }else{
            //没有数据
            tv_nodata.setVisibility(View.VISIBLE);
            tv_nodata.setText("请求网络失败/...");
        }
        progressbar.setVisibility(View.GONE);
        //把下拉刷新的状态还原
        refresh.finishRefresh();
    }

    /**
     * 解析json数据并且返回列表
     * @param json -手动解析
     * @return
     */
    private ArrayList<MedioItem> parsedJson(String json) {
        ArrayList<MedioItem> medioItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            //optJSONArray 解析比 getJSONArray好
            JSONArray jsonArr = jsonObject.optJSONArray("trailers");
            if (jsonArr != null && jsonArr.length() > 0) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject item = (JSONObject) jsonArr.get(i);
                    if (item != null) {
                        //创建类
                        MedioItem medioItem = new MedioItem();

                        String name = item.optString("movieName");
                        medioItem.setName(name);
                        String desc = item.optString("videoTitle");
                        medioItem.setDesc(desc);
                        String data = item.optString("url");
                        medioItem.setData(data);
                        String imageUrl = item.optString("coverImg");
                        medioItem.setImageUrl(imageUrl);
                        long duration = item.optLong("videoLength");
                        medioItem.setDuration(duration);
                        //添加到集合中去
                        medioItems.add(medioItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return medioItems;
    }
}
