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
    public static String BASE_URL = "http://52.78.11.153/";

    public static SharedPreferences sSharedPreferences = null;

    // SharedPreferences 키 값
    public static String TAG = "HERETHERE_APP";

    // JWT Token 값
    public static String X_ACCESS_TOKEN = "X-ACCESS-TOKEN";

    //날짜 형식
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    //알림 채널
    public static final String CHANNEL_ID = "we_to_channel";

    // Retrofit 인스턴스
    public static Retrofit retrofit;

    public static final int LOITERING_DELAY = 3000;          // 위치감지시 최소 머무는시간 (1000 = 1초, 특정장소에 ?초에상 머물러야 그곳에 도착했다고 인식함, 왓다갓다 계속할경우 알림안드도록)
    public static final int GPS_LADIUS = 150;         //항상

    public static final int NONE = 55;             // 정보없는 일정
    public static final int TIME = 66;             // 시간일정
    public static final int LOCATION = 77;         // 위치일정

    public static final int AT_START = 11;         // 출발
    public static final int AT_ARRIVE = 22;        // 도착
    public static final int AT_NEAR = 33;          // 근처

    public static final int ALWAYS = 100;         //항상
    public static final int MORNING = 200;        //아침  6~12
    public static final int EVENING = 300;        //오후  12~21
    public static final int NIGHT = 400;          //밤    21~06

    public static final int ALL_DAY = 1;         //매일
    public static final int WEEK_DAY = 2;        //매주
    public static final int MONTH_DAY = 3;        //매월
    public static final int ONE_DAY = 4;          //특정일

    public static final int NOREPEAT = 1;         //반복없음
    public static final int DAYREPEAT = 2;        //매일반복
    public static final int WEEKREPEAT = 3;       //매주반복
    public static final int MONTHREPEAT = 4;      //매달반복

    public static final int NO_DATA = -1;          //의미없는값표시


    public static final int HOME_SELECT_MODE = 500;      //매달반복
    public static final int COMPANY_SELECT_MODE = 600;      //매달반복
    public static final int SCHOOL_SELECT_MODE = 700;      //매달반복
    public static final int OTHER_SELECT_MODE = 800;      //매달반복

    public static String fcmToken = "";

    private static ApplicationClass applicationClass;
    public static ApplicationClass getApplicationClassContext() {
        if (applicationClass == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return applicationClass;
    }


    @Override
    public void onCreate() {
        super.onCreate();
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
        applicationClass=null;
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
