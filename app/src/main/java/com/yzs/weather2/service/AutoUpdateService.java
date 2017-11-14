package com.yzs.weather2.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.yzs.weather2.R;
import com.yzs.weather2.activity.WeatherActivity;
import com.yzs.weather2.gson.Weather;
import com.yzs.weather2.util.HttpUtil;
import com.yzs.weather2.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notification();
    }

    private void notification() {
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //        builder.setContentTitle("这是标题");
        builder.setContentText(cityName + "        " + degree + "        " + weatherInfo);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.cloudy);
        builder.setColor(Color.LTGRAY);
        builder.setContentIntent(pendingIntent);
        Notification build = builder.build();
        startForeground(1, build);
    }

    String cityName = "";
    String degree = "";
    String weatherInfo = "";
    int i;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cityName = intent.getStringExtra("cityName");
        degree = intent.getStringExtra("degree");
        weatherInfo = intent.getStringExtra("weatherInfo");
        i = intent.getIntExtra("stop", 0);
        Log.i("aaa", "intent****i:: "+i);

        if (i == 2) {
            stopForeground(true);
            i = 1;
            Log.i("aaa", "stopForeground****i:: "+i);
        }
        updateWeather();
        updateBingPic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        notification();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=054d3f0c518e49388b0f43f6ea547d53";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().toString();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null) {
                        SharedPreferences.Editor ditor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        ditor.putString("weather", responseText);
                        ditor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新必应图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().toString();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
