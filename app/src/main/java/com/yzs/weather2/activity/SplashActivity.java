package com.yzs.weather2.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.fonts.MeteoconsModule;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;
import com.joanzapata.iconify.fonts.TypiconsModule;
import com.joanzapata.iconify.fonts.WeathericonsModule;
import com.yzs.weather2.R;

/**
 * 启动欢迎页面
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        // 字体图标
        Iconify.with(new FontAwesomeModule())
                .with(new EntypoModule())
                .with(new TypiconsModule())
                .with(new MaterialModule())
                .with(new MaterialCommunityModule())
                .with(new MeteoconsModule())
                .with(new WeathericonsModule())
                .with(new SimpleLineIconsModule())
                .with(new IoniconsModule());
        checkPermission();
    }

    /**
     * 请求开通权限的数组
     */
    private static final String[] PERMISSION_STORAGE = {Manifest.permission.INTERNET};
    //请求码
    private static final int REQUEST_EXTERNAL_STORAGE = 9527;//权限请求码

    /**
     * 检查权限
     * Android 6.0 以上的系统需要动态检查权限
     */
    private void checkPermission() {

        //检查系统是都允许该权限
        //=====参数1：context上下文（MainActivity.this）
        //=====参数2：要检查的权限（Manifest.permission.xxx）
        int peemission = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (peemission != PackageManager.PERMISSION_GRANTED) {
            // 系统没有授予该权限  先申请该权限，在回调中再调用测试代码。比如Android6.0以上设备
            // 参数1  请求获取权限的activity 比如MainActivity.this
            // 参数2 请求获取权限的数组。给该Activity开通哪些权限
            // 参数3 请求码。理论上可以是任意值
            ActivityCompat.requestPermissions(SplashActivity.this, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            // 系统已经授予该权限 直接调用测试代码。比如5.1以下设备，或者6.0以上设置中已经授予过的权限
            checkService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkService();
        }
    }

    private void checkService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
