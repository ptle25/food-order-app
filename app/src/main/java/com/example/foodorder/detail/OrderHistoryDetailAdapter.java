package com.example.foodorder.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorder.R;
import com.example.foodorder.model.CartModel;

import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryDetailAdapter extends RecyclerView.Adapter<OrderHistoryDetailAdapter.OrderHistoryDetailViewHolder> {

    List<CartModel> list;


    public OrderHistoryDetailAdapter(List<CartModel> list) {
        this.list = list;
    }

    public List<CartModel> getList() {
        return list;
    }

    @NonNull
    @Override
    public OrderHistoryDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHistoryDetailViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_order_detail_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryDetailViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void initView() {

    }


    class OrderHistoryDetailViewHolder extends RecyclerView.ViewHolder {
        private AppCompatCheckBox radioButton;
        private ImageView imageView2;
        private TextView name;
        private TextView description;
        private TextView price;
        private ImageView imgDelete;
        private TextView btnTru;
        private TextView tvSoLuong;
        private TextView btnCong;
        private TextView tongTien;

        public OrderHistoryDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radioButton);
            imageView2 = itemView.findViewById(R.id.imageView2);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            btnTru = itemView.findViewById(R.id.btnTru);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            btnCong = itemView.findViewById(R.id.btnCong);
            tongTien = itemView.findViewById(R.id.tongTien);
        }

        public void onBind(int position) {
            CartModel item = list.get(position);
            if (item != null) {
                Glide.with(itemView.getContext()).load(item.getImage()).into(imageView2);
                name.setText(item.getName());
                description.setText(item.getDescription());
                DecimalFormat formatter = new DecimalFormat("#,###");
                price.setText(formatter.format(item.getPrice()) + " VNĐ");
                tvSoLuong.setText("SL: " + item.getAmount() + "");


                tongTien.setText("Tổng: " + formatter.format(item.getAmount() * item.getPrice()) + " VNĐ");
            }
        }
    }
}
