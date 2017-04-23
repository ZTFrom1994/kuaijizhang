package com.example.kuaijizhang;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.spot.SplashView;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 功能：使用ViewPager实现初次进入应用时的引导页
 * 
 * (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入引导activity；否，则进入MainActivity
 * (3)5s后执行(2)操作
 * 
 * @author sz082093
 *
 */
public class SplashActivity extends Activity {
	private final String appId="e8407a3c25792339";
	private final String appSecret="24795f6bafe8ee4c";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean mFirst = isFirstEnter(SplashActivity.this,SplashActivity.this.getClass().getName());
        if(mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,50);
        else
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,50);
        
    }   
    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private boolean isFirstEnter(Context context,String className){
        if(context==null || className==null||"".equalsIgnoreCase(className))return false;
        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                 .getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
        if(mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }
     
     
    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
            case SWITCH_MAINACTIVITY:
                Intent mIntent = new Intent();
                mIntent.setClass(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mIntent);
                SplashActivity.this.finish();
                break;
            case SWITCH_GUIDACTIVITY:
                mIntent = new Intent();
                mIntent.setClass(SplashActivity.this, GuideActivity.class);
                SplashActivity.this.startActivity(mIntent);
                SplashActivity.this.finish();
                break;
            }
            super.handleMessage(msg);
        }
    };
}