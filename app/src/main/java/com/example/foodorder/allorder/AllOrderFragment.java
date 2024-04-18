package com.example.foodorder.allorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.foodorder.databinding.FragmentAllOrderBinding;
import com.example.foodorder.detail.OrderHistoryDetailActivity;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.history.OrderHistoryAdapter;

import com.example.foodorder.model.OrderHistoryModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class AllOrderFragment extends Fragment implements OrderHistoryAdapter.OnItemClickListener {

    private FragmentAllOrderBinding binding;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OrderHistoryAdapter adapter;
    private List<OrderHistoryModel> list = new ArrayList<>();

    public AllOrderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListOrder();
    }

    private void getListOrder() {
        list = new ArrayList<>();
        ProgressHelper.showDialog(getContext(), "Đang lấy dữ liệu");
        db.collection("order")
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

    @Override
    public void onClick(int position) {
        Intent intent = new Intent(getContext(), OrderHistoryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("documentID", list.get(position).getDocumentID());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
