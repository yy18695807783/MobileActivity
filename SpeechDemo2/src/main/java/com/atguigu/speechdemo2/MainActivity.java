package com.atguigu.speechdemo2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends Activity {

    private EditText et_input;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
// 请勿在“ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57fd9c46");
        setContentView(R.layout.activity_main);
        et_input = (EditText) findViewById(R.id.et_input);
    }

    public void inputText(View view){
//        Toast.makeText(MainActivity.this, "语音输入", Toast.LENGTH_SHORT).show();
        showInputDialogI();
    }

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

            et_input.setText(content);
            et_input.setSelection(et_input.length());
            Log.e("TAG",result);
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

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {

            if(i != ErrorCode.SUCCESS){
                Toast.makeText(MainActivity.this, "初始化出错了...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void speechText(View view){
//        Toast.makeText(MainActivity.this, "文字读取", Toast.LENGTH_SHORT).show();
        speechText();
    }

    private void speechText() {
        //1.创建 SpeechSynthesizer 对象, 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(this, null);
//2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
//设置发音人（更多在线发音人，用户可参见 附录13.2
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaokun"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
//设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
//保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
//仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
//3.开始合成
        mTts.startSpeaking(et_input.getText().toString(), mSynListener);

    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener(){
        //会话结束回调接口，没有错误时， error为null
        public void onCompleted(SpeechError error) {}
//缓冲进度回调
//percent为缓冲进度0~100， beginPos为缓冲音频在文本中开始位置， endPos表示缓冲音频在
//        文本中结束位置， info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        //开始播放
        public void onSpeakBegin() {}
        //暂停播放
        public void onSpeakPaused() {}
//播放进度回调
//percent为播放进度0~100,beginPos为播放音频在文本中开始位置， endPos表示播放音频在文
//        本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        //恢复播放回调接口
        public void onSpeakResumed() {}
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
    };

}
