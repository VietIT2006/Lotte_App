package com.ptithcm.lottemart.features.shipper;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.DeliveryApiService;
import com.ptithcm.lottemart.data.remote.RetrofitClient;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperWalletActivity extends AppCompatActivity {

    private TextView tvWalletBalance;
    private TextView tvEmptyTransactions;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private DeliveryApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_wallet);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        tvEmptyTransactions = findViewById(R.id.tvEmptyTransactions);
        rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TransactionAdapter();
        rvTransactions.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(DeliveryApiService.class);
        loadWallet();

        findViewById(R.id.btnTopupWallet).setOnClickListener(v -> {
            String[] options = {"50.000đ", "100.000đ", "200.000đ", "500.000đ"};
            final double[] values = {50000, 100000, 200000, 500000};
            
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn số tiền muốn nạp")
                .setItems(options, (dialog, which) -> {
                    double amount = values[which];
                    performTopup(amount);
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    private void performTopup(double amount) {
        Toast.makeText(this, "Đang khởi tạo cổng thanh toán PayOS...", Toast.LENGTH_SHORT).show();
        apiService.createWalletPayosLink(new DeliveryApiService.PayosWalletLinkRequest(amount))
            .enqueue(new Callback<ApiResponse<DeliveryApiService.PayosWalletLinkResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<DeliveryApiService.PayosWalletLinkResponse>> call, Response<ApiResponse<DeliveryApiService.PayosWalletLinkResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        DeliveryApiService.PayosWalletLinkResponse data = response.body().getData();
                        android.content.Intent intent = new android.content.Intent(ShipperWalletActivity.this, com.ptithcm.lottemart.features.shopping.PayosPaymentActivity.class);
                        intent.putExtra("PAYMENT_URL", data.checkoutUrl);
                        intent.putExtra("ORDER_CODE", data.orderCode);
                        intent.putExtra("TOTAL_AMOUNT", amount);
                        intent.putExtra("TYPE", "wallet_topup");
                        startActivityForResult(intent, 2002);
                    } else {
                        Toast.makeText(ShipperWalletActivity.this, "Không tạo được liên kết nạp tiền PayOS", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<DeliveryApiService.PayosWalletLinkResponse>> call, Throwable t) {
                    Toast.makeText(ShipperWalletActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2002) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Nạp tiền ví thành công!", Toast.LENGTH_LONG).show();
                loadWallet();
            } else {
                Toast.makeText(this, "Nạp tiền ví đã bị hủy hoặc thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadWallet() {
        apiService.getWalletInfo().enqueue(new Callback<ApiResponse<DeliveryApiService.WalletInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeliveryApiService.WalletInfo>> call, Response<ApiResponse<DeliveryApiService.WalletInfo>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DeliveryApiService.WalletInfo info = response.body().getData();
                    if (info != null) {
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        tvWalletBalance.setText(format.format(info.balance));

                        if (info.transactions != null && !info.transactions.isEmpty()) {
                            adapter.setTransactions(info.transactions);
                            tvEmptyTransactions.setVisibility(View.GONE);
                            rvTransactions.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyTransactions.setVisibility(View.VISIBLE);
                            rvTransactions.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(ShipperWalletActivity.this, "Lỗi tải thông tin ví từ máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DeliveryApiService.WalletInfo>> call, Throwable t) {
                Toast.makeText(ShipperWalletActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TxViewHolder> {
        private List<DeliveryApiService.WalletTransaction> list = new ArrayList<>();

        public void setTransactions(List<DeliveryApiService.WalletTransaction> txs) {
            this.list = txs;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_transaction, parent, false);
            return new TxViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
            DeliveryApiService.WalletTransaction tx = list.get(position);

            holder.tvTxTitle.setText(tx.description);
            
            // Format Amount
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String sign = "+";
            if ("debit".equalsIgnoreCase(tx.type)) {
                sign = "-";
                holder.tvTxAmount.setTextColor(Color.parseColor("#D32F2F"));
                holder.tvTxAmount.setText(sign + format.format(tx.amount));
                holder.ivTxIcon.setImageResource(android.graphics.drawable.Icon.createWithResource(holder.itemView.getContext(), android.R.drawable.ic_menu_upload).getResId()); // Or simple drawable
                holder.ivTxIcon.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.ivTxIcon.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#D32F2F")));
            } else {
                holder.tvTxAmount.setTextColor(Color.parseColor("#388E3C"));
                holder.tvTxAmount.setText(sign + format.format(tx.amount));
                // default is green plus icon
                holder.ivTxIcon.setImageResource(android.R.drawable.ic_input_add);
                holder.ivTxIcon.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.ivTxIcon.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#388E3C")));
            }

            // Format Date
            if (tx.created_at != null) {
                try {
                    // Try parsing ISO date
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = inputFormat.parse(tx.created_at);
                    
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
                    holder.tvTxDate.setText(outputFormat.format(date));
                } catch (Exception e) {
                    holder.tvTxDate.setText(tx.created_at);
                }
            }
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        static class TxViewHolder extends RecyclerView.ViewHolder {
            ImageView ivTxIcon;
            TextView tvTxTitle, tvTxDate, tvTxAmount;

            public TxViewHolder(@NonNull View itemView) {
                super(itemView);
                ivTxIcon = itemView.findViewById(R.id.ivTxIcon);
                tvTxTitle = itemView.findViewById(R.id.tvTxTitle);
                tvTxDate = itemView.findViewById(R.id.tvTxDate);
                tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
            }
        }
    }
}
