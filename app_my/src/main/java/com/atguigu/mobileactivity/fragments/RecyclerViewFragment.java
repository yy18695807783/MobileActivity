package com.atguigu.mobileactivity.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.adapter.RecycleAdapter;
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

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;

/**
 * Created by 颜银 on 2016/9/28.
 * QQ:443098360
 * 微信：y443098360
 */
public class RecyclerViewFragment extends BaseFragment {
    //    private TextView textView;
    private ProgressBar progressbar;
    private TextView tv_nomedia;
    private RecyclerView rv_recyclerView;
    private List<BaiSiBean.ListBean> list;
    private RecycleAdapter adapter;
    private MaterialRefreshLayout refresh;

    @Override
    public View initView() {
        LogUtil.e("RecyclerViewUI创建了");
        View view = View.inflate(context, R.layout.fragment_recyclerviewfrangment, null);
        rv_recyclerView = (RecyclerView) view.findViewById(R.id.rv_recyclerView);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            /**
             * 下拉刷新
             * @param materialRefreshLayout
             */
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                getDataFromNet();
            }

            /**
             * 加载更多
             * @param materialRefreshLayout
             */
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                getMoreDataFromNet();
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("RecyclerView数据绑定了");
//        textView.setText("RecyclerView");
        getDataFromNet();
    }

    private void getDataFromNet() {
        final RequestParams params = new RequestParams(Constants.BAI_SI_BU_DE_JIE);
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
                list = parsedJson(result);
                if (list != null && list.size() > 0) {
                    //有视频
                    tv_nomedia.setVisibility(View.GONE);
                    rv_recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    adapter = new RecycleAdapter(context, list);
                    rv_recyclerView.setAdapter(adapter);
                    rv_recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                        @Override
                        public void onChildViewAttachedToWindow(View view) {

                        }

                        @Override
                        public void onChildViewDetachedFromWindow(View view) {
                            if (JCVideoPlayerManager.listener() != null) {
                                JCVideoPlayer videoPlayer = (JCVideoPlayer) JCVideoPlayerManager.listener();
                                if (((ViewGroup) view).indexOfChild(videoPlayer) != -1 && videoPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                                    JCVideoPlayer.releaseAllVideos();
                                }
                            }
                        }
                    });

                    rv_recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

                } else {
                    //没有视频
                    tv_nomedia.setVisibility(View.VISIBLE);
                }
                progressbar.setVisibility(View.GONE);
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

    public void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.BAI_SI_BU_DE_JIE);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess==" + result);
                processMoreData(result);
                refresh.finishRefreshLoadMore();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processMoreData(String json) {

        list.addAll(parsedJson(json));
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }
}
