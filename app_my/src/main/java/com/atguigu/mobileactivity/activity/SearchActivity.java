package com.atguigu.mobileactivity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileactivity.R;
import com.atguigu.mobileactivity.adapter.SearcheAdapter;
import com.atguigu.mobileactivity.domain.SearchBean;
import com.atguigu.mobileactivity.utils.Constants;
import com.atguigu.mobileactivity.utils.JsonParser;
import com.atguigu.mobileactivity.utils.LogUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchActivity extends Activity implements View.OnClickListener {

    private EditText etSearch;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tvNodata;
    private SearcheAdapter adapter;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-10-12 13:34:05 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_search);
        etSearch = (EditText) findViewById(R.id.et_search);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listview = (ListView) findViewById(R.id.listview);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);

        //设置点击事件
        ivVoice.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57fd9c46");//57fdc5a0  我的
        findViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice:
//                Toast.makeText(this,"语音输入",Toast.LENGTH_LONG).show();
                showInputDialogI();

                break;
            case R.id.tv_search:
//                Toast.makeText(this, "搜索", Toast.LENGTH_LONG).show();
                gotoSeachData();
                break;
        }
    }

    /**
     * 点击搜索的回调
     */
    private void gotoSeachData() {
        progressbar.setVisibility(View.VISIBLE);
        String word = etSearch.getText().toString().trim();
        if(word != null){
            try {
                word = URLEncoder.encode(word, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = Constants.NET_SEARCH_URL +word;

            getDataFromNet(url);
        }
    }

    private void getDataFromNet(String url) {
        RequestParams paranms = new RequestParams(url);
        x.http().get(paranms, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求成功==" + result);
                //解析和显示数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求失败==" + ex.getMessage());
                progressbar.setVisibility(View.GONE);
                tvNodata.setVisibility(View.VISIBLE);
                tvNodata.setText("没有搜索到内容...");
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

    private void processData(String json) {
        SearchBean bean = parseJson(json);
        List<SearchBean.ItemsEntity> list =  bean.getItems();
        if(list != null && list.size() >0){
            //有数据
            tvNodata.setVisibility(View.GONE);
            //设置适配器
            adapter = new SearcheAdapter(this,list);
            listview.setAdapter(adapter);

        }else{
            if(adapter != null){
                adapter = new SearcheAdapter(this,list);
                listview.setAdapter(adapter);
            }
            //没有数据
            tvNodata.setVisibility(View.VISIBLE);
            tvNodata.setText("没有搜索到内容...");
        }

        //隐藏
        progressbar.setVisibility(View.GONE);
    }
    /**
     * gson解析数据
     * @param json
     * @return
     */
    private SearchBean parseJson(String json) {
        return new Gson().fromJson(json, SearchBean.class);
    }

    /**
     * 点击声音输入的回调
     */
    private void showInputDialogI() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();

    }
    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {

            if(i != ErrorCode.SUCCESS){
                Toast.makeText(SearchActivity.this, "初始化出错了...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * 返回结果
         * @param results
         * @param b
         */
        @Override
        public void onResult(RecognizerResult results, boolean b) {
            String result = results.getResultString();
            String text = JsonParser.parseIatResult(result);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }


            String content = resultBuffer.toString();
            content = content.replace("。","");

            etSearch.setText(content);
            etSearch.setSelection(etSearch.length());
            Log.e("TAG", result);
//            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

        }

        /**
         * 语音输入失败
         * @param speechError
         */
        @Override
        public void onError(SpeechError speechError) {

        }
    }

//    public void speechText(View view){
////        Toast.makeText(MainActivity.this, "文字读取", Toast.LENGTH_SHORT).show();
//        speechText();
//    }
//
//    private void speechText() {
//        //1.创建 SpeechSynthesizer 对象, 第二个参数： 本地合成时传 InitListener
//        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(this, null);
////2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
////设置发音人（更多在线发音人，用户可参见 附录13.2
////        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaokun"); //设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
////设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
////保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
////仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
////3.开始合成
//        mTts.startSpeaking(etSearch.getText().toString(), mSynListener);
//
//    }
//
//    //合成监听器
//    private SynthesizerListener mSynListener = new SynthesizerListener(){
//        //会话结束回调接口，没有错误时， error为null
//        public void onCompleted(SpeechError error) {}
//        //缓冲进度回调
////percent为缓冲进度0~100， beginPos为缓冲音频在文本中开始位置， endPos表示缓冲音频在
////        文本中结束位置， info为附加信息。
//        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
//        //开始播放
//        public void onSpeakBegin() {}
//        //暂停播放
//        public void onSpeakPaused() {}
//        //播放进度回调
////percent为播放进度0~100,beginPos为播放音频在文本中开始位置， endPos表示播放音频在文
////        本中结束位置.
//        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
//        //恢复播放回调接口
//        public void onSpeakResumed() {}
//        //会话事件回调接口
//        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
//    };


}
