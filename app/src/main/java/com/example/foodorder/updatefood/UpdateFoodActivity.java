package com.example.foodorder.updatefood;



import static com.example.foodorder.foodorder.ShowMessageHelper.showMessage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.example.foodorder.databinding.ActivityUpdateFoodBinding;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.model.FoodModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

public class UpdateFoodActivity extends AppCompatActivity {

    private ActivityUpdateFoodBinding binding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String documentID;
    private FoodModel foodModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        documentID = (String) Objects.requireNonNull(getIntent().getExtras()).get("documentID");
        if (documentID != null) {
            binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(binding.name.getText())) {
                        showMessage(UpdateFoodActivity.this, "Không được để trống tên");
                    } else if (TextUtils.isEmpty(binding.description.getText())) {
                        showMessage(UpdateFoodActivity.this, "Không được để trống mô tả");
                    } else if (TextUtils.isEmpty(binding.price.getText())) {
                        showMessage(UpdateFoodActivity.this, "Không được để trống giá");
                    } else {
                        ProgressHelper.showDialog(UpdateFoodActivity.this, "Đang update");
                        HashMap<String, Object> food = new HashMap<>();
                        food.put("name", binding.name.getText().toString());
                        food.put("price", Integer.parseInt(removeDot(binding.price.getText().toString())));
                        food.put("description", binding.description.getText().toString());
                        firestore.collection("foods").document(documentID).update(food).addOnSuccessListener(unused -> {
                            ProgressHelper.dismissDialog();
                            showMessage(UpdateFoodActivity.this, "Update thành công");
                        }).addOnFailureListener(e -> ProgressHelper.dismissDialog());
                    }

                }
            });
        }

        foodModel = (FoodModel) Objects.requireNonNull(getIntent().getExtras()).get("data");
        if (foodModel != null) {
            binding.toolBarLayout.setTitle(foodModel.getName());
            Glide.with(this).load(foodModel.getImage()).into(binding.imgProduct);
            binding.name.setText(foodModel.getName());
            binding.description.setText(foodModel.getDescription());
            DecimalFormat formatter = new DecimalFormat("#,###");
            binding.price.setText(formatter.format(foodModel.getPrice()));
        }
    }

    private String removeDot(String text) {
        if (text.contains(".")) {
            return text.replace(".", "");
        }
        return text;
    }
}