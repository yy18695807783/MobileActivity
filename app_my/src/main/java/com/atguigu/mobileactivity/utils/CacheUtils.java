package com.atguigu.mobileactivity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.atguigu.mobileactivity.service.MusicPlayerService;

/**
 * Created by 颜银 on 2016/10/8.
 * QQ:443098360
 * 微信：y443098360
 * 作用：缓存工具类-共享偏好
 */
public class CacheUtils {
    /**
     * 缓存文本数据
     * @param context
     * @param key
     * @param result
     */
    public static void putString(Context context, String key, String result) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);//私有化
        sp.edit().putString(key,result).commit();//别忘记提交，保存到内存中
    }

    /**
     * 取出缓存数据 得到文本信息
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getString(key,"");//不要写null  比较会出空指针，用key实际是Url来取出Json数据
    }

    /**
     * 得到播放模式
     * @param context
     * @param key
     * @return
     */
    public static int getPlaymode(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
//        return sp.getInt(key, MusicPlayerService.REPEAT_NORMAL);
        return sp.getInt(key, MusicPlayerService.REPEAT_NORMAL);//没有返回默认的模式，顺序循环模式
    }

    public static void savePlaymode(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
}
