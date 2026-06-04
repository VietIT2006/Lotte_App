package com.ptithcm.lottemart.features.shipper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ptithcm.lottemart.R;
import com.ptithcm.lottemart.data.models.ShipperOrder;
import java.util.ArrayList;
import java.util.List;

public class ShipperOrderAdapter extends RecyclerView.Adapter<ShipperOrderAdapter.OrderViewHolder> {

    private List<ShipperOrder> orderList = new ArrayList<>();
    private OnOrderActionClickListener listener;

    public interface OnOrderActionClickListener {
        void onActionClick(ShipperOrder order);
    }

    public void setListener(OnOrderActionClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<ShipperOrder> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shipper_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ShipperOrder order = orderList.get(position);
        holder.tvOrderId.setText("Mã đơn: #" + order.getId().substring(0, 8));
        holder.tvOrderPrice.setText("COD: " + order.getTotalAmount() + " đ");
        
        holder.btnUpdateStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderAddress, tvOrderPrice;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderAddress = itemView.findViewById(R.id.tvOrderAddress);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
