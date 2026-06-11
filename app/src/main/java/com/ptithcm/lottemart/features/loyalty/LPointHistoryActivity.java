package com.ptithcm.lottemart.features.loyalty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.api.ApiResponse;
import com.ptithcm.lottemart.data.api.LoyaltyApiService;
import com.ptithcm.lottemart.data.local.SessionManager;
import com.ptithcm.lottemart.data.remote.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LPointHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private TransactionAdapter adapter;
    private List<LoyaltyApiService.Transaction> transactions = new ArrayList<>();
    private SessionManager sessionManager;
    private LoyaltyApiService loyaltyApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lpoint_history);

        sessionManager = new SessionManager(this);
        loyaltyApiService = RetrofitClient.getClient().create(LoyaltyApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvHistory = findViewById(R.id.rvHistory);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        rvHistory.setAdapter(adapter);

        fetchHistory();
    }

    private void fetchHistory() {
        String token = "Bearer " + sessionManager.getAuthToken();
        loyaltyApiService.getTransactionHistory(token).enqueue(new Callback<ApiResponse<List<LoyaltyApiService.Transaction>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<LoyaltyApiService.Transaction>>> call, Response<ApiResponse<List<LoyaltyApiService.Transaction>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    transactions = response.body().getData();
                    adapter.notifyDataSetChanged();
                    updateUI();
                } else {
                    Toast.makeText(LPointHistoryActivity.this, "Không thể tải lịch sử giao dịch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<LoyaltyApiService.Transaction>>> call, Throwable t) {
                Toast.makeText(LPointHistoryActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (transactions.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }

    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LPointHistoryActivity.this).inflate(R.layout.item_lpoint_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LoyaltyApiService.Transaction tx = transactions.get(position);
            holder.tvReason.setText(tx.getReason());
            
            // Format time cleanly
            String dateStr = tx.getCreatedAt();
            if (dateStr != null && dateStr.contains("T")) {
                dateStr = dateStr.split("T")[0];
            }
            holder.tvTime.setText(dateStr);

            int amount = tx.getAmount();
            if (amount > 0) {
                holder.tvAmount.setText(String.format("+%,d P", amount));
                holder.tvAmount.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                holder.tvAmount.setText(String.format("%,d P", amount));
                holder.tvAmount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvReason, tvTime, tvAmount;

            ViewHolder(View view) {
                super(view);
                tvReason = view.findViewById(R.id.tvReason);
                tvTime = view.findViewById(R.id.tvTime);
                tvAmount = view.findViewById(R.id.tvAmount);
            }
        }
    }
}
