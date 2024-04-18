package com.example.foodorder.detail;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.example.foodorder.databinding.ActivityOrderHistoryDetailBinding;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.foodorder.SharedPref;
import com.example.foodorder.model.OrderModel;
import com.example.foodorder.model.UserData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Objects;

public class OrderHistoryDetailActivity extends AppCompatActivity {

    private ActivityOrderHistoryDetailBinding binding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private UserData userData;
    String documentID;
    private OrderHistoryDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);
        documentID = (String) Objects.requireNonNull(getIntent().getExtras()).get("documentID");
        getDetailOrder();
    }

    private void getDetailOrder() {
        if (userData != null && documentID != null) {
            ProgressHelper.showDialog(this, "Đang lấy dữ liệu");
            firestore.collection("order").document(documentID).
                    get().addOnFailureListener(e -> {
                        ProgressHelper.dismissDialog();

                    }).addOnCompleteListener(task -> {
                        ProgressHelper.dismissDialog();
                        if (task.isSuccessful()) {
                            OrderModel orderModel = task.getResult().toObject(OrderModel.class);
                            assert orderModel != null;
                            adapter = new OrderHistoryDetailAdapter(orderModel.getFoods());
                            binding.recyclerView.setAdapter(adapter);
                        }
                    });

        }
    }
}