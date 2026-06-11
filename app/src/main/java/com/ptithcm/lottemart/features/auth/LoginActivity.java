package com.ptithcm.lottemart.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.AuthApiService;
import com.ptithcm.lottemart.data.api.AuthResponseData;
import com.ptithcm.lottemart.data.api.LoginRequest;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import com.ptithcm.lottemart.features.fogotPassword.ForgotPasswordActivity;
import com.ptithcm.lottemart.utils.Validator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import org.json.JSONObject;
import com.ptithcm.lottemart.data.api.SocialLoginRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private android.widget.Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private SessionManager sessionManager;
    private AuthApiService authApiService;
    
    // Social Login
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.ptithcm.lottemart.data.remote.RetrofitClient.init(this);
        
        sessionManager = new SessionManager(this);
        authApiService = RetrofitClient.getClient().create(AuthApiService.class);
        
        // Kiểm tra Tự động đăng nhập
        if (sessionManager.isLoggedIn()) {
            if ("admin".equalsIgnoreCase(sessionManager.getUserRole()) || "superAdmin".equalsIgnoreCase(sessionManager.getUserRole())) {
                navigateToAdminMain();
            } else if ("shipper".equalsIgnoreCase(sessionManager.getUserRole())) {
                navigateToShipperMain();
            } else {
                navigateToMain();
            }
            return;
        }

        setContentView(R.layout.user_activity_login);

        initViews();
        setupListeners();
        setupSocialLogin();
    }

    private void setupSocialLogin() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configure Facebook Login
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook login success");
                fetchFacebookUserProfile(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook login cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook login error", error);
                Toast.makeText(LoginActivity.this, "Đăng nhập Facebook thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFacebookUserProfile(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    try {
                        String email = object.getString("email");
                        String name = object.getString("name");
                        String id = object.getString("id");
                        String avatar = "https://graph.facebook.com/" + id + "/picture?type=large";
                        
                        performSocialLogin(email, name, avatar, "facebook", id, null);
                    } catch (Exception e) {
                        Log.e(TAG, "Error fetching Facebook profile", e);
                        Toast.makeText(LoginActivity.this, "Không thể lấy thông tin Facebook", Toast.LENGTH_SHORT).show();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnGoogle).setOnClickListener(v -> signInWithGoogle());
            
        findViewById(R.id.btnFacebook).setOnClickListener(v -> signInWithFacebook());

        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập Email của bạn");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }

        if (!isValid) return;

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        LoginRequest loginRequest = new LoginRequest(email, password);
        authApiService.login(loginRequest).enqueue(new Callback<ApiResponse<AuthResponseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseData>> call, Response<ApiResponse<AuthResponseData>> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponseData data = response.body().getData();
                    sessionManager.saveAuthToken(
                            data.getToken(),
                            data.getUser().getId(),
                            data.getUser().getFullName(),
                            data.getUser().getEmail(),
                            data.getUser().getRole()
                    );
                    
                    // CẬP NHẬT TOKEN VÀO HỆ THỐNG MẠNG NGAY LẬP TỨC
                    com.ptithcm.lottemart.data.remote.RetrofitClient.init(LoginActivity.this);
                    
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    
                    if ("admin".equalsIgnoreCase(data.getUser().getRole()) || "superAdmin".equalsIgnoreCase(data.getUser().getRole())) {
                        navigateToAdminMain();
                    } else if ("shipper".equalsIgnoreCase(data.getUser().getRole())) {
                        navigateToShipperMain();
                    } else {
                        navigateToMain();
                    }
                } else {
                    String errorMsg = "Email hoặc mật khẩu không hợp lệ";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseData>> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");
                Log.e(TAG, "Login failure", t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                performSocialLogin(
                    account.getEmail(),
                    account.getDisplayName(),
                    account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "",
                    "google",
                    account.getId(),
                    account.getIdToken()
                );
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSocialLogin(String email, String name, String avatar, String provider, String providerId, String idToken) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email từ tài khoản mạng xã hội", Toast.LENGTH_LONG).show();
            return;
        }

        btnLogin.setEnabled(false);
        SocialLoginRequest request = new SocialLoginRequest(email, name, avatar, provider, providerId, idToken);
        
        authApiService.socialLogin(request).enqueue(new Callback<ApiResponse<AuthResponseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponseData>> call, Response<ApiResponse<AuthResponseData>> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponseData data = response.body().getData();
                    sessionManager.saveAuthToken(
                            data.getToken(),
                            data.getUser().getId(),
                            data.getUser().getFullName(),
                            data.getUser().getEmail(),
                            data.getUser().getRole()
                    );
                    
                    // Cập nhật Retrofit với token mới
                    com.ptithcm.lottemart.data.remote.RetrofitClient.init(LoginActivity.this);
                    
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    if ("admin".equalsIgnoreCase(data.getUser().getRole()) || "superAdmin".equalsIgnoreCase(data.getUser().getRole())) {
                        navigateToAdminMain();
                    } else if ("shipper".equalsIgnoreCase(data.getUser().getRole())) {
                        navigateToShipperMain();
                    } else {
                        navigateToMain();
                    }
                } else {
                    String errorMsg = "Lỗi đăng nhập mạng xã hội";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponseData>> call, Throwable t) {
                btnLogin.setEnabled(true);
                Log.e(TAG, "Social login failure", t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, com.ptithcm.lottemart.MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminMain() {
        Intent intent = new Intent(LoginActivity.this, com.ptithcm.lottemart.features.admin.AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToShipperMain() {
        Intent intent = new Intent(LoginActivity.this, com.ptithcm.lottemart.features.shipper.ShipperDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
