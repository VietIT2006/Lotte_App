package com.ptithcm.lottemart.features.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class AdminRolesActivity extends BaseAdminActivity {
    private RecyclerView rvList;
    private AdminRoleAdapter adapter;
    private SessionManager sessionManager;
    private UserApiService apiService;
    private boolean isSuperAdmin;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_roles);

        setHeaderTitle("Quản lý Phân quyền");

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(UserApiService.class);
        
        String role = sessionManager.getUserRole();
        isSuperAdmin = "superAdmin".equalsIgnoreCase(role);
        currentUserEmail = sessionManager.getUserEmail();

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminRoleAdapter(this, new ArrayList<>(), isSuperAdmin, new AdminRoleAdapter.OnRoleActionListener() {
            @Override
            public void onEdit(User user) {
                showEditRoleDialog(user);
            }
        });
        rvList.setAdapter(adapter);

        fetchUsers();
    }

    private void fetchUsers() {
        String token = "Bearer " + sessionManager.getAuthToken();
        apiService.getUsers(token).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<User> allUsers = response.body().getData();
                    List<User> displayUsers = new ArrayList<>();

                    if (isSuperAdmin) {
                        // SuperAdmin sees all admins and superAdmins, or everyone
                        displayUsers.addAll(allUsers);
                    } else {
                        // Normal admin only sees themselves
                        for (User u : allUsers) {
                            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(currentUserEmail)) {
                                displayUsers.add(u);
                                break;
                            }
                        }
                    }
                    adapter.setUsers(displayUsers);
                } else {
                    Toast.makeText(AdminRolesActivity.this, "Lỗi lấy danh sách quyền", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(AdminRolesActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditRoleDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa quyền: " + user.getUsername());

        String[] roles = {"customer", "admin", "shipper", "superAdmin"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, roles);

        int checkedItem = 0;
        String currentRole = user.getRole() != null ? user.getRole() : "customer";
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(currentRole)) {
                checkedItem = i;
                break;
            }
        }

        builder.setSingleChoiceItems(arrayAdapter, checkedItem, (dialog, which) -> {
            String selectedRole = roles[which];
            dialog.dismiss();
            if (!selectedRole.equalsIgnoreCase(currentRole)) {
                updateRole(user.getId(), selectedRole);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateRole(String userId, String newRole) {
        String token = "Bearer " + sessionManager.getAuthToken();
        UserApiService.RoleUpdateRequest request = new UserApiService.RoleUpdateRequest(newRole);
        
        apiService.updateUserRole(token, userId, request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminRolesActivity.this, "Đổi quyền thành công", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    Toast.makeText(AdminRolesActivity.this, "Lỗi đổi quyền", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(AdminRolesActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
