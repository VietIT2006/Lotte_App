package com.ptithcm.lottemart.features.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.UserApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.User;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserManagementActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private AdminUserAdapter adapter;
    private TextInputEditText etSearchUser;
    private ChipGroup chipGroupRole;
    private SessionManager sessionManager;
    private List<User> allUsers = new ArrayList<>();
    private boolean isSuperAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_management);

        sessionManager = new SessionManager(this);
        isSuperAdmin = "superAdmin".equalsIgnoreCase(sessionManager.getUserRole());

        rvUsers = findViewById(R.id.rvUsers);
        etSearchUser = findViewById(R.id.etSearchUser);
        chipGroupRole = findViewById(R.id.chipGroupRole);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, isSuperAdmin);
        rvUsers.setAdapter(adapter);

        findViewById(R.id.fabAddUser).setOnClickListener(v -> {
            Toast.makeText(this, "Xem danh sách Khiếu nại (Đang phát triển)", Toast.LENGTH_SHORT).show();
            // TODO: Mở AdminComplaintActivity
        });

        setupFilters();
        fetchUsers();
    }

    private void setupFilters() {
        // Lọc theo search box
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lọc theo Chip (All, Admins, Customers)
        chipGroupRole.setOnCheckedChangeListener((group, checkedId) -> applyFilters());
    }

    private void applyFilters() {
        String query = etSearchUser.getText() != null ? etSearchUser.getText().toString().toLowerCase().trim() : "";
        int checkedChipId = chipGroupRole.getCheckedChipId();
        
        List<User> filteredList = new ArrayList<>();
        for (User user : allUsers) {
            boolean matchesSearch = false;
            if (user.getFullName() != null && user.getFullName().toLowerCase().contains(query)) matchesSearch = true;
            if (user.getUsername() != null && user.getUsername().toLowerCase().contains(query)) matchesSearch = true;

            boolean matchesRole = true;
            if (checkedChipId == R.id.chipAdmins) {
                matchesRole = "admin".equalsIgnoreCase(user.getRole()) || "superAdmin".equalsIgnoreCase(user.getRole());
            } else if (checkedChipId == R.id.chipCustomers) {
                matchesRole = "customer".equalsIgnoreCase(user.getRole());
            } // R.id.chipAll -> matchesRole is true

            if (matchesSearch && matchesRole) {
                filteredList.add(user);
            }
        }
        adapter.setUsers(filteredList);
    }

    private void fetchUsers() {
        UserApiService apiService = RetrofitClient.getClient().create(UserApiService.class);
        String token = "Bearer " + sessionManager.getAuthToken();
        
        apiService.getUsers(token).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allUsers = response.body().getData();
                    applyFilters(); // Hiển thị list sau khi apply filter
                } else {
                    Toast.makeText(AdminUserManagementActivity.this, "Lỗi lấy danh sách User", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(AdminUserManagementActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
