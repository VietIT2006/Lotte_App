package com.ptithcm.lottemart.features.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.UserApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.User;
import com.ptithcm.lottemart.data.remote.NetworkConfig;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText etFullName, etPhone, etAddress;
    private android.widget.Button btnSave;
    private ImageButton btnBack, btnChangeAvatar;
    private ImageView ivAvatar;
    private SessionManager sessionManager;
    private UserApiService userApiService;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).circleCrop().into(ivAvatar);
                    uploadAvatar();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_edit_profile);

        sessionManager = new SessionManager(this);
        userApiService = RetrofitClient.getClient().create(UserApiService.class);

        initViews();
        setupListeners();
        loadUserProfile();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        ivAvatar = findViewById(R.id.ivAvatar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());
        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });
    }

    private void loadUserProfile() {
        String token = "Bearer " + sessionManager.getAuthToken();
        userApiService.getProfile(token).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    etFullName.setText(user.getFullName() != null ? user.getFullName() : "");
                    etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                    etAddress.setText(user.getAddress() != null ? user.getAddress() : "");

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        String avatarUrl = user.getAvatar();
                        if (avatarUrl.startsWith("/uploads")) {
                            avatarUrl = NetworkConfig.getBaseDomain() + avatarUrl;
                        }
                        Glide.with(EditProfileActivity.this).load(avatarUrl).circleCrop().into(ivAvatar);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatar() {
        if (selectedImageUri == null) return;

        File file = getFileFromUri(selectedImageUri);
        if (file == null) {
            Toast.makeText(this, "Không thể lấy ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        String token = "Bearer " + sessionManager.getAuthToken();
        userApiService.uploadAvatar(token, body).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật ảnh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            File tempFile = new File(getCacheDir(), "avatar_upload.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveProfile() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        UserApiService.UserProfileUpdateRequest request = new UserApiService.UserProfileUpdateRequest(fullName, phone, address);
        String token = "Bearer " + sessionManager.getAuthToken();

        userApiService.updateProfile(token, request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu thay đổi");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    sessionManager.saveAuthToken(
                            sessionManager.getAuthToken(),
                            sessionManager.getUserId(),
                            fullName,
                            sessionManager.getUserEmail(),
                            sessionManager.getUserRole()
                    );
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Lưu thay đổi");
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
