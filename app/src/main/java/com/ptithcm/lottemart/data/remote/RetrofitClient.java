package com.ptithcm.lottemart.data.remote;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.features.auth.LoginActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Context mContext = null;

    // Khởi tạo context để lấy Token từ SessionManager
    public static void init(Context context) {
        mContext = context.getApplicationContext();
        reset(); // Reset để khởi tạo lại với Interceptor mới
    }

    public static void reset() {
        retrofit = null;
    }

    public static Retrofit getClient() {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(NetworkConfig.BASE_URL)) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new AuthInterceptor()) // Thêm bộ lọc bảo mật
                    .connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static Retrofit mapRetrofit = null;
    public static Retrofit getMapClient() {
        if (mapRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();

            mapRetrofit = new Retrofit.Builder()
                    .baseUrl("https://dummy.com/") // baseUrl bắt buộc phải có, nhưng sẽ bị ghi đè bởi @Url
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return mapRetrofit;
    }

    // Bộ lọc tự động gắn Token và bắt lỗi 401 (Hết hạn)
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();

            // 1. Tự động gắn Token nếu có (Trừ các request Đăng nhập/Đăng ký)
            String path = originalRequest.url().encodedPath();
            boolean isAuthPath = path.contains("/auth/login") || path.contains("/auth/register");

            if (!isAuthPath && mContext != null) {
                SessionManager sessionManager = new SessionManager(mContext);
                String token = sessionManager.getAuthToken();
                if (token != null && !token.isEmpty()) {
                    builder.header("Authorization", "Bearer " + token);
                }
            }

            Response response = chain.proceed(builder.build());

            // 2. Bắt lỗi 401 - Token hết hạn hoặc sai
            if (response.code() == 401 && mContext != null && !isAuthPath) {
                handleUnauthorized();
            }

            return response;
        }

        private void handleUnauthorized() {
            if (mContext == null) return;
            
            new Handler(Looper.getMainLooper()).post(() -> {
                // Chỉ hiện thông báo một lần
                Toast.makeText(mContext, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                
                SessionManager sessionManager = new SessionManager(mContext);
                sessionManager.logout();

                // Reset Retrofit để các request tiếp theo không gửi token cũ
                reset();

                // Đẩy người dùng về màn hình Login và xóa toàn bộ stack trước đó
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
            });
        }
    }
}
