package com.example.foodorder.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.foodorder.databinding.FragmentOrderHistoryBinding;

import com.example.foodorder.detail.OrderHistoryDetailActivity;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.foodorder.SharedPref;
import com.example.foodorder.model.OrderHistoryModel;
import com.example.foodorder.model.UserData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class OrderHistoryFragment extends Fragment implements OrderHistoryAdapter.OnItemClickListener {

    private FragmentOrderHistoryBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData userData;
    private OrderHistoryAdapter adapter;
    private List<OrderHistoryModel> list = new ArrayList<>();

    public OrderHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);
        getListOrder();
    }

    private void getListOrder() {
        if (userData != null) {
            list = new ArrayList<>();
            ProgressHelper.showDialog(getContext(), "Đang lấy dữ liệu");
            db.collection("order").whereEqualTo("userID", userData.getDocumentID())
                    .get().addOnCompleteListener(task -> {
                        ProgressHelper.dismissDialog();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OrderHistoryModel model = document.toObject(OrderHistoryModel.class);
                                model.setDocumentID(document.getId());
                                list.add(model);
                            }
                        }
                        adapter = new OrderHistoryAdapter(list);
                        adapter.setOnItemClickListener(this);
                        binding.recyclerView.setAdapter(adapter);

                    }).addOnFailureListener(e -> {
                        ProgressHelper.dismissDialog();
                    });
        }
    }

    @Override
    public void onClick(int position) {
        Intent intent = new Intent(getContext(), OrderHistoryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("documentID", list.get(position).getDocumentID());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}