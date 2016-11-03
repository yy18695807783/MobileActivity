package com.atguigu.mobileactivity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.activity.ShowImageAndGifActivity;
import com.atguigu.mobileactivity.domain.BaiSiBean;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.atguigu.mobileactivity.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by 颜银 on 2016/10/13.
 * QQ:443098360
 * 微信：y443098360
 * 作用：
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private Utils utils;
    private ImageOptions imageOptions;

    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    private List<BaiSiBean.ListBean> baiSiBeans;
    private static Context context;


    public RecycleAdapter(Context context, List<BaiSiBean.ListBean> baiSiBeans) {
        this.baiSiBeans = baiSiBeans;
        this.context = context;
        utils = new Utils();
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(80))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.video_default_icon)
                .setFailureDrawableId(R.drawable.video_default_icon)
                .build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemViewType = viewType;//当前itme是什么类型
        View convertView = null;
        //初始化item布局
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                convertView = View.inflate(context, R.layout.all_video_item, null);
                break;
            case TYPE_IMAGE://图片
                convertView = View.inflate(context, R.layout.all_image_item, null);
                break;
            case TYPE_TEXT://文字
                convertView = View.inflate(context, R.layout.all_text_item, null);
                break;
            case TYPE_GIF://gif
                convertView = View.inflate(context, R.layout.all_gif_item, null);
                break;
            case TYPE_AD://软件广告
                convertView = View.inflate(context, R.layout.all_ad_item, null);
                break;
        }
        return new ViewHolder(convertView, itemViewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindShowData(position, getItemViewType(position), holder);
    }


    /**
     * 设置数据
     *
     * @param datas
     */
    public void setData(List<BaiSiBean.ListBean> datas) {
        this.baiSiBeans = datas;
    }

    @Override
    public int getItemCount() {

        return baiSiBeans.size();
    }

    private void bindShowData(int position, int itemViewType, ViewHolder viewHolder) {
        //根据位置得到数据,绑定数据
        BaiSiBean.ListBean mediaItem = baiSiBeans.get(position);
        utils = new Utils();
        LogUtil.e("根据位置得到数据绑定数据  bindShowData()");
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                bindData(viewHolder, mediaItem);
                //第一个参数是视频播放地址，第二个参数是显示封面的地址，第三参数是标题
                //                viewHolder.jcv_videoplayer.setUp(mediaItem.getVideo().getVideo().get(0),JCVideoPlayer.SCREEN_LAYOUT_LIST, "");

                boolean setUp = viewHolder.jcv_videoplayer.setUp(
                        mediaItem.getVideo().getVideo().get(0), JCVideoPlayer.SCREEN_LAYOUT_LIST,
                        "");
                //加载图片
                if (setUp) {
                    ImageLoader.getInstance().displayImage(mediaItem.getVideo().getThumbnail().get(0),
                            viewHolder.jcv_videoplayer.thumbImageView);
                }

                viewHolder.tv_play_nums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
                viewHolder.tv_video_duration.setText(utils.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");
                LogUtil.e("视频数据绑定");
                break;
            case TYPE_IMAGE://图片
                bindData(viewHolder, mediaItem);
                viewHolder.iv_image_icon.setImageResource(R.drawable.bg_item);
//                int height = mediaItem.getImage().getHeight() <= DensityUtil.getScreenHeight() * 0.75 ? mediaItem.getImage().getHeight() : (int) (DensityUtil.getScreenHeight() * 0.75);

//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.getScreenWidth(), height);
//                viewHolder.iv_image_icon.setLayoutParams(params);
                if (mediaItem.getImage() != null && mediaItem.getImage().getBig() != null && mediaItem.getImage().getBig().size() > 0) {
//                    x.image().bind(viewHolder.iv_image_icon, mediaItem.getImage().getBig().get(0).trim());
                    Glide.with(context).load(mediaItem.getImage().getBig().get(0)).placeholder(R.drawable.video_default_icon).error(R.drawable.video_default_icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.iv_image_icon);
                }
                LogUtil.e("图片数据绑定------------------------" + mediaItem.getImage().getBig().get(0).trim());
                break;
            case TYPE_TEXT://文字
                bindData(viewHolder, mediaItem);
                LogUtil.e("文字数据绑定");
                break;
            case TYPE_GIF://gif
                bindData(viewHolder, mediaItem);
                System.out.println("mediaItem.getGif().getImages().get(0)" + mediaItem.getGif().getImages().get(0));
                Glide.with(context).load(mediaItem.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.iv_image_gif);
                LogUtil.e("gif数据绑定");
                break;
            case TYPE_AD://软件广告
                break;
        }


        //设置文本
        viewHolder.tv_context.setText(mediaItem.getText() + "_" + mediaItem.getType());

    }


    //2.绑定公共数据

    private void bindData(ViewHolder viewHolder, BaiSiBean.ListBean mediaItem) {
        if (mediaItem.getU() != null && mediaItem.getU().getHeader() != null && mediaItem.getU().getHeader().get(0) != null) {
            x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0), imageOptions);
//            x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0));
        }
        if (mediaItem.getU() != null && mediaItem.getU().getName() != null) {
            viewHolder.tv_name.setText(mediaItem.getU().getName() + "");
        }

        viewHolder.tv_time_refresh.setText(mediaItem.getPasstime());

        //设置标签
        List<BaiSiBean.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }

        //设置点赞，踩,转发
        viewHolder.tv_shenhe_ding_number.setText(mediaItem.getUp());
        viewHolder.tv_shenhe_cai_number.setText(mediaItem.getDown() + "");
        LogUtil.e("------mediaItem.getForward() = " + mediaItem.getForward());

        viewHolder.tv_posts_number.setText(mediaItem.getForward() + "");

    }


    class ViewHolder extends RecyclerView.ViewHolder {
        //user_info
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;
        //bottom
        ImageView iv_video_kind;
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;

        //中间公共部分 -所有的都有
        TextView tv_context;


        //Video
//        TextView tv_context;
        TextView tv_play_nums;
        TextView tv_video_duration;
        ImageView iv_commant;
        TextView tv_commant_context;
        //        JCVideoPlayer jcv_videoplayer;
        JCVideoPlayerStandard jcv_videoplayer;
        //Image
        ImageView iv_image_icon;
//        TextView tv_context;

        //Text
//        TextView tv_context;

        //Gif
        GifImageView iv_image_gif;
//        TextView tv_context;

        //软件推广
        Button btn_install;
//        TextView iv_image_icon;
        //TextView tv_context;

        public ViewHolder(View convertView, int viewType) {
            super(convertView);

            switch (viewType) {
                case TYPE_VIDEO://视频
                    //在这里实例化特有的
                    tv_play_nums = (TextView) convertView.findViewById(R.id.tv_play_nums);
                    tv_video_duration = (TextView) convertView.findViewById(R.id.tv_video_duration);
                    iv_commant = (ImageView) convertView.findViewById(R.id.iv_commant);
                    tv_commant_context = (TextView) convertView.findViewById(R.id.tv_commant_context);
                    jcv_videoplayer = (JCVideoPlayerStandard) convertView.findViewById(R.id.jcv_videoplayer);
                    break;
                case TYPE_IMAGE://图片
                    iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                    break;
                case TYPE_TEXT://文字
                    break;
                case TYPE_GIF://gif
                    iv_image_gif = (GifImageView) convertView.findViewById(R.id.iv_image_gif);
                    break;
                case TYPE_AD://软件广告
                    btn_install = (Button) convertView.findViewById(R.id.btn_install);
                    iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                    break;
            }
            switch (viewType) {
                case TYPE_VIDEO://视频
                case TYPE_IMAGE://图片
                case TYPE_TEXT://文字
                case TYPE_GIF://gif
                    //加载除开广告部分的公共部分视图
                    //user info
                    iv_headpic = (ImageView) convertView.findViewById(R.id.iv_headpic);
                    tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    tv_time_refresh = (TextView) convertView.findViewById(R.id.tv_time_refresh);
                    iv_right_more = (ImageView) convertView.findViewById(R.id.iv_right_more);
                    //bottom
                    iv_video_kind = (ImageView) convertView.findViewById(R.id.iv_video_kind);
                    tv_video_kind_text = (TextView) convertView.findViewById(R.id.tv_video_kind_text);
                    tv_shenhe_ding_number = (TextView) convertView.findViewById(R.id.tv_shenhe_ding_number);
                    tv_shenhe_cai_number = (TextView) convertView.findViewById(R.id.tv_shenhe_cai_number);
                    LogUtil.e("底部初始化--------------------------");
                    tv_posts_number = (TextView) convertView.findViewById(R.id.tv_posts_number);
                    ll_download = (LinearLayout) convertView.findViewById(R.id.ll_download);
                    break;
            }
            //中间公共部分 -所有的都有
            tv_context = (TextView) convertView.findViewById(R.id.tv_context);


            //设置item的点击事件
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaiSiBean.ListBean listEntity = baiSiBeans.get(getLayoutPosition());
                    if (listEntity != null) {
                        Intent intent = new Intent(context, ShowImageAndGifActivity.class);
                        if (listEntity.getType().equals("gif")) {
                            String url = listEntity.getGif().getImages().get(0);
                            intent.putExtra("url", url);
                            context.startActivity(intent);
                        } else if (listEntity.getType().equals("image")) {
                            String url = listEntity.getImage().getBig().get(0);
                            intent.putExtra("url", url);
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }

    }


    /**
     * 当前item是什么类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int itemViewType = -1;
        //根据位置，从列表中得到一个数据对象
        BaiSiBean.ListBean listEntity = baiSiBeans.get(position);
        String type = listEntity.getType();//得到类型
        LogUtil.e("type===" + type);
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;//广播
        }
        return itemViewType;
    }

}
