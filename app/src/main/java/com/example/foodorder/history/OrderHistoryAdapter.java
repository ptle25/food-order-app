package com.example.foodorder.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.R;
import com.example.foodorder.model.CartModel;
import com.example.foodorder.model.OrderHistoryModel;

import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {


    List<OrderHistoryModel> list;


    public OrderHistoryAdapter(List<OrderHistoryModel> list) {
        this.list = list;
    }

    public List<OrderHistoryModel> getList() {
        return list;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHistoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_order_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void initView() {

    }

    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMaDonHang;
        private TextView tvNgayDat;
        private TextView tvSoLuong;
        private TextView tvTongTien;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaDonHang = itemView.findViewById(R.id.tvMaDonHang);
            tvNgayDat = itemView.findViewById(R.id.tvNgayDat);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvTongTien = itemView.findViewById(R.id.tvTongTien);
            if (onItemClickListener != null) {
                itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
            }
        }

        public void onBind(int position) {
            OrderHistoryModel item = list.get(position);
            if (item != null) {
                tvMaDonHang.setText("Mã đơn hàng: " + item.getDocumentID());
                tvNgayDat.setText("Ngày đặt hàng " + item.getDateOrder());
                if (item.getFoods() != null && item.getFoods().size() > 0) {
                    tvSoLuong.setText(item.getFoods().size() + " sản phẩm");
                    Long tongtien = 0L;
                    for (CartModel cartModel : item.getFoods()) {
                        tongtien += cartModel.getPrice() * cartModel.getAmount();
                    }
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    tvTongTien.setText("Tổng tiền: " + formatter.format(tongtien) + " VNĐ");
                }

            }
        }
    }

    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
