package com.ninano.weto.src;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kakao.auth.KakaoSDK;
import com.ninano.weto.config.XAccessTokenInterceptor;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationClass extends Application {

    public static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=uft-8");
    public static MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");


    // 테스트 서버 주소
//    public static String BASE_URL = "http://www.so-yo.info/";
    // 실서버 주소
    public static String BASE_URL = "http://52.79.232.21/";

    public static SharedPreferences sSharedPreferences = null;

    // SharedPreferences 키 값
    public static String TAG = "HERETHERE_APP";

    // JWT Token 값
    public static String X_ACCESS_TOKEN = "X-ACCESS-TOKEN";

    //날짜 형식
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    // Retrofit 인스턴스
    public static Retrofit retrofit;

    public static final int NONE = 55;             // 정보없는 일정
    public static final int TIME = 66;             // 시간일정
    public static final int LOCATION = 77;         // 위치일정

    public static final int AT_START = 11;         // 출발
    public static final int AT_ARRIVE = 22;        // 도착
    public static final int AT_NEAR = 33;          // 근처

    public static final int ALWAYS = 100;         //항상
    public static final int MORNING = 200;        //아침
    public static final int EVENING = 300;        //오후
    public static final int NIGHT = 400;          //밤

    private static volatile ApplicationClass instance = null;

    public static ApplicationClass getApplicationClassContext() {
        if (instance == null)
            throw new IllegalStateException("Error of ApplicationClass");
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (sSharedPreferences == null) {
            sSharedPreferences = getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }

        applicationClass = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        if (sSharedPreferences == null) {
            sSharedPreferences = getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        applicationClass = null;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
