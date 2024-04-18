package com.example.foodorder.foodorder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityDetailFoodBinding;
import com.example.foodorder.model.FoodModel;
import com.example.foodorder.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

public class DetailFoodActivity extends AppCompatActivity {

    private ActivityDetailFoodBinding binding;
    private FoodModel foodModel;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);
        setOnClick();
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolBarLayout);

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            // Completely collapsed
            // Not collapsed
            // Ẩn tiêu đề
            collapsingToolbarLayout.setTitleEnabled(Math.abs(verticalOffset) == appBarLayout1.getTotalScrollRange()); // Hiện tiêu đề
        });
        foodModel = (FoodModel) Objects.requireNonNull(getIntent().getExtras()).get("data");
        if (foodModel != null) {
            binding.toolBarLayout.setTitle(foodModel.getName());
            Glide.with(this).load(foodModel.getImage()).into(binding.imgProduct);
            binding.name.setText(foodModel.getName());
            binding.description.setText(foodModel.getDescription());
            DecimalFormat formatter = new DecimalFormat("#,###");
            binding.price.setText(formatter.format(foodModel.getPrice()) + " VNĐ");
        }
    }

    private void setOnClick() {
        binding.btnCong.setOnClickListener(view -> {
            int soluong = Integer.parseInt(binding.tvSoLuong.getText().toString());
            soluong++;
            binding.tvSoLuong.setText(soluong + "");
        });

        binding.btnTru.setOnClickListener(view -> {
            int soluong = Integer.parseInt(binding.tvSoLuong.getText().toString());
            if (soluong == 1) {
                binding.tvSoLuong.setText(1 + "");
                return;
            }
            soluong--;
            binding.tvSoLuong.setText(soluong + "");
        });

        binding.btnAddCart.setOnClickListener(view -> {
            if (foodModel != null && userData != null) {
                ProgressHelper.showDialog(this, "Thêm giỏ hàng");
                db.collection("cart").whereEqualTo("userID", userData.getDocumentID())
                        .whereEqualTo("foodID", foodModel.getDocumentID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            int amountOld = 0;
                                            if (document.get("amount", Integer.class) != null) {
                                                amountOld = document.get("amount", Integer.class);
                                            }
                                            int amountUpdate = amountOld + Integer.parseInt(binding.tvSoLuong.getText().toString());
                                            db.collection("cart").document(document.getId())
                                                    .update("amount", amountUpdate)
                                                    .addOnCompleteListener(task1 -> {
                                                        ProgressHelper.dismissDialog();
                                                        ShowMessageHelper.showMessage(DetailFoodActivity.this, "Thêm giỏ hàng thành công");
                                                    }).addOnFailureListener(e -> {
                                                        ProgressHelper.dismissDialog();
                                                    });
                                            ;
                                        }

                                    } else {
                                        HashMap<String, Object> cart = new HashMap<>();
                                        cart.put("userID", userData.getDocumentID());
                                        cart.put("name", foodModel.getName());
                                        cart.put("image", foodModel.getImage());
                                        cart.put("description", foodModel.getDescription());
                                        cart.put("amount", Integer.parseInt(binding.tvSoLuong.getText().toString()));
                                        cart.put("price", foodModel.getPrice());
                                        cart.put("foodID", foodModel.getDocumentID());
                                        db.collection("cart").add(cart).addOnCompleteListener(task2 -> {
                                            ProgressHelper.dismissDialog();
                                            ShowMessageHelper.showMessage(DetailFoodActivity.this, "Thêm giỏ hàng thành công");
                                        }).addOnFailureListener(e -> {
                                            ProgressHelper.dismissDialog();
                                        });
                                    }
                                }
                            }
                        });

            }
        });
    }
}