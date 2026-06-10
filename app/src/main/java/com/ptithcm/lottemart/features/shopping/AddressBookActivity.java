package com.ptithcm.lottemart.features.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.models.Address;
import java.util.ArrayList;
import java.util.List;

public class AddressBookActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_MAP_PICKER = 1003;
    private static final String TAG = "AddressBookActivity";

    private com.google.android.material.textfield.TextInputEditText etStreetRef;
    private com.google.android.material.textfield.TextInputEditText etWardRef;
    private com.google.android.material.textfield.TextInputEditText etDistrictRef;
    private com.google.android.material.textfield.TextInputEditText etCityRef;

    private RecyclerView rvAddresses;
    private AddressAdapter adapter;
    private List<Address> addressList;
    private SessionManager sessionManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE_AUTO_FILL = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_address_book);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Khởi tạo danh sách địa chỉ và lọc theo tài khoản đăng nhập
        String currentUserId = sessionManager.getUserId();
        addressList = sessionManager.getAddressList(currentUserId);

        if (addressList == null) {
            addressList = new ArrayList<>();
            if ("000000000000000000000001".equals(currentUserId)) {
                // Lọc ra các địa chỉ dành riêng cho Admin
                addressList.add(new Address("1", "Admin Lotte", "0901234567", "469 Nguyễn Hữu Thọ", "Tân Hưng", "Quận 7", "TP. HCM", true, "home"));
                addressList.add(new Address("2", "Admin Văn Phòng", "0918765432", "37 Hùng Vương", "Phường 4", "Quận 5", "TP. HCM", false, "office"));
            } else if ("69c9daead9fb80416235e662".equals(currentUserId)) {
                // Lọc ra các địa chỉ dành riêng cho tài khoản phamcongt56@gmail.com
                addressList.add(new Address("1", "Thành Phạm Công", "0846183771", "12 Lê Duẩn", "Bến Nghé", "Quận 1", "TP. HCM", true, "home"));
                addressList.add(new Address("2", "Phạm Công (Công Ty)", "0909999888", "100 Đường số 7", "Phường Tân Phong", "Quận 7", "TP. HCM", false, "office"));
            } else {
                // Các tài khoản khác
                addressList.add(new Address("1", sessionManager.getUserName(), "0900000000", "Địa chỉ mẫu của bạn", "Phường Bến Thành", "Quận 1", "TP. HCM", true, "home"));
            }
            sessionManager.saveAddressList(currentUserId, addressList);
        }

        rvAddresses = findViewById(R.id.rvAddresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AddressAdapter(this, addressList, new AddressAdapter.OnAddressClickListener() {
            @Override
            public void onSelect(Address address) {
                Intent intent = new Intent();
                intent.putExtra("selected_address", address);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onDelete(Address address, int position) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(AddressBookActivity.this)
                    .setTitle("Xóa địa chỉ")
                    .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        addressList.remove(position);
                        sessionManager.saveAddressList(currentUserId, addressList);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, addressList.size());
                        Toast.makeText(AddressBookActivity.this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            }
        });
        
        rvAddresses.setAdapter(adapter);

        MaterialButton btnAddAddress = findViewById(R.id.btnAddAddress);
        btnAddAddress.setOnClickListener(v -> showAddAddressDialog());
    }

    private void showAddAddressDialog() {
        android.view.View dialogView = android.view.LayoutInflater.from(this).inflate(R.layout.dialog_add_address, null);
        
        com.google.android.material.textfield.TextInputLayout tilName = dialogView.findViewById(R.id.tilName);
        com.google.android.material.textfield.TextInputLayout tilPhone = dialogView.findViewById(R.id.tilPhone);
        com.google.android.material.textfield.TextInputLayout tilStreet = dialogView.findViewById(R.id.tilStreet);
        com.google.android.material.textfield.TextInputLayout tilWard = dialogView.findViewById(R.id.tilWard);
        com.google.android.material.textfield.TextInputLayout tilDistrict = dialogView.findViewById(R.id.tilDistrict);
        com.google.android.material.textfield.TextInputLayout tilCity = dialogView.findViewById(R.id.tilCity);

        com.google.android.material.textfield.TextInputEditText etName = dialogView.findViewById(R.id.etName);
        com.google.android.material.textfield.TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        com.google.android.material.textfield.TextInputEditText etStreet = dialogView.findViewById(R.id.etStreet);
        com.google.android.material.textfield.TextInputEditText etWard = dialogView.findViewById(R.id.etWard);
        com.google.android.material.textfield.TextInputEditText etDistrict = dialogView.findViewById(R.id.etDistrict);
        com.google.android.material.textfield.TextInputEditText etCity = dialogView.findViewById(R.id.etCity);
        android.widget.RadioGroup rgLabel = dialogView.findViewById(R.id.rgLabel);
        androidx.appcompat.widget.SwitchCompat swDefault = dialogView.findViewById(R.id.swDefault);
        com.google.android.material.button.MaterialButton btnVerifyOnMap = dialogView.findViewById(R.id.btnVerifyOnMap);
        com.google.android.material.button.MaterialButton btnPickFromMap = dialogView.findViewById(R.id.btnPickFromMap);

        if (btnPickFromMap != null) {
            btnPickFromMap.setOnClickListener(v -> {
                etStreetRef = etStreet;
                etWardRef = etWard;
                etDistrictRef = etDistrict;
                etCityRef = etCity;

                Intent intent = new Intent(AddressBookActivity.this, MapPickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAP_PICKER);
            });
        }

        // Click to verify map location using OpenStreetMap Nominatim and Google Maps
        if (btnVerifyOnMap != null) {
            btnVerifyOnMap.setOnClickListener(v -> {
                String street = etStreet.getText().toString().trim();
                String ward = etWard.getText().toString().trim();
                String district = etDistrict.getText().toString().trim();
                String city = etCity.getText().toString().trim();
                
                if (street.isEmpty() || ward.isEmpty() || district.isEmpty() || city.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập địa chỉ đầy đủ để kiểm tra trên bản đồ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fullAddress = street + ", " + ward + ", " + district + ", " + city;
                Intent intent = new Intent(this, com.ptithcm.lottemart.features.admin.AdminUserMapActivity.class);
                intent.putExtra("USER_ADDRESS", fullAddress);
                intent.putExtra("USER_NAME", etName.getText().toString().trim().isEmpty() ? "Người nhận" : etName.getText().toString().trim());
                intent.putExtra("USER_PHONE", etPhone.getText().toString().trim().isEmpty() ? "Chưa cập nhật SĐT" : etPhone.getText().toString().trim());
                intent.putExtra("USER_ROLE", "Khách hàng");
                startActivity(intent);
            });
        }

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Thêm địa chỉ mới")
            .setView(dialogView)
            .setPositiveButton("Lưu", null) // Set to null to handle manually below
            .setNegativeButton("Hủy", null)
            .create();

        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String street = etStreet.getText().toString().trim();
            String ward = etWard.getText().toString().trim();
            String district = etDistrict.getText().toString().trim();
            String city = etCity.getText().toString().trim();

            boolean isValid = true;

            if (name.isEmpty()) {
                tilName.setError("Vui lòng nhập họ tên người nhận");
                isValid = false;
            } else {
                tilName.setError(null);
            }

            if (phone.isEmpty()) {
                tilPhone.setError("Vui lòng nhập số điện thoại");
                isValid = false;
            } else {
                tilPhone.setError(null);
            }

            if (street.isEmpty()) {
                tilStreet.setError("Vui lòng nhập số nhà, tên đường");
                isValid = false;
            } else {
                tilStreet.setError(null);
            }

            if (ward.isEmpty()) {
                tilWard.setError("Vui lòng nhập Phường / Xã");
                isValid = false;
            } else {
                tilWard.setError(null);
            }

            if (district.isEmpty()) {
                tilDistrict.setError("Vui lòng nhập Quận / Huyện");
                isValid = false;
            } else {
                tilDistrict.setError(null);
            }

            if (city.isEmpty()) {
                tilCity.setError("Vui lòng nhập Tỉnh / Thành phố");
                isValid = false;
            } else {
                tilCity.setError(null);
            }

            if (!isValid) {
                return;
            }

            String label = "home";
            int checkedLabelId = rgLabel.getCheckedRadioButtonId();
            if (checkedLabelId == R.id.rbOffice) {
                label = "office";
            }

            boolean isDefault = swDefault.isChecked();
            if (isDefault) {
                for (Address addr : addressList) {
                    addr.setDefault(false);
                }
            }

            String newId = String.valueOf(System.currentTimeMillis());
            Address address = new Address(newId, name, phone, street, ward, district, city, isDefault, label);
            addressList.add(address);
            sessionManager.saveAddressList(currentUserId, addressList);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã thêm địa chỉ mới thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_MAP_PICKER) {
                String street = data.getStringExtra("street");
                String ward = data.getStringExtra("ward");
                String district = data.getStringExtra("district");
                String city = data.getStringExtra("city");

                if (etStreetRef != null) etStreetRef.setText(street);
                if (etWardRef != null) etWardRef.setText(ward);
                if (etDistrictRef != null) etDistrictRef.setText(district);
                if (etCityRef != null) etCityRef.setText(city);
                
                Toast.makeText(this, "Đã tự động định vị địa chỉ từ bản đồ!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
