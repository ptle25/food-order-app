package com.example.foodorder.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorder.R;
import com.example.foodorder.model.FoodModel;

import java.text.DecimalFormat;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<FoodModel> list;

    public List<FoodModel> getList() {
        return list;
    }

    public HomeAdapter(List<FoodModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void initView() {

    }

    public void updateList(List<FoodModel> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView textView2;
        private TextView textView3;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onDelete(getAdapterPosition());
                    return true;
                }
                return false;
            });
        }

        public void onBind(int position) {
            FoodModel item = list.get(position);

            Glide.with(itemView.getContext())
                    .load(item.getImage())
                    .into(imageView);
            textView.setText(item.getName());
            textView2.setText(item.getDescription());

            DecimalFormat formatter = new DecimalFormat("#,###");
            textView3.setText(formatter.format(item.getPrice()) + " VNĐ");
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onDelete(int positon);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
