package com.example.foodorder.cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;


import com.example.foodorder.ChiTietDatHangActivity;
import com.example.foodorder.databinding.FragmentCartBinding;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.foodorder.SharedPref;
import com.example.foodorder.foodorder.ShowMessageHelper;
import com.example.foodorder.model.CartModel;
import com.example.foodorder.model.OrderModel;
import com.example.foodorder.model.UserData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;


public class CartFragment extends Fragment implements CartAdapter.OnItemClickListener {
    private FragmentCartBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData userData;
    private List<CartModel> list = new ArrayList<>();
    private CartAdapter adapter;

    public CartFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getListCart();
    }

    private void getListCart() {
        if (userData != null) {
            ProgressHelper.showDialog(getContext(), "Đang lấy dữ liệu");
            db.collection("cart").whereEqualTo("userID", userData.getDocumentID()).get().addOnCompleteListener(task -> {
                ProgressHelper.dismissDialog();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CartModel cartModel = new CartModel();
                        cartModel.setDocumentID(document.getId());
                        cartModel.setImage(document.getString("image"));
                        cartModel.setPrice(document.getLong("price"));
                        cartModel.setDescription(document.getString("description"));
                        cartModel.setName(document.getString("name"));
                        cartModel.setAmount(document.get("amount", Integer.class));
                        list.add(cartModel);
                    }
                    adapter = new CartAdapter(list);
                    adapter.setOnItemClickListener(this);
                    binding.recyclerView.setAdapter(adapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                    binding.recyclerView.addItemDecoration(dividerItemDecoration);
                    binding.recyclerView.setHasFixedSize(true);
                }
            }).addOnFailureListener(e -> {
                ProgressHelper.dismissDialog();
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClick();
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);
        list.clear();

    }

    private void setOnClick() {
        binding.btnOrder.setOnClickListener(v -> {
            final boolean[] canOrder = new boolean[1];
            list.forEach(new Consumer<CartModel>() {
                @Override
                public void accept(CartModel cartModel) {
                    if (cartModel.isCheck()) {
                        canOrder[0] = true;
                    }
                }
            });
            if (canOrder[0] == true) {
                Intent intent = new Intent(getContext(), ChiTietDatHangActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable) adapter.getList());
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                ShowMessageHelper.showMessage(getContext(), "Bạn chưa chọn sản phẩm");
            }
        });

    }

    private void order() {
        ProgressHelper.showDialog(getContext(), "Đang order");
        OrderModel orderModel = new OrderModel();
        orderModel.setDateOrder(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        orderModel.setUserID(userData.getDocumentID());
        orderModel.setFoods(getListFood());
        db.collection("order").add(orderModel).addOnCompleteListener(task -> {
            ProgressHelper.dismissDialog();
            ShowMessageHelper.showMessage(getContext(), "Order thành công");
            removeOrder();
            setTotalPrice();
        }).addOnFailureListener(e -> ProgressHelper.dismissDialog());
    }
    private void removeOrder() {
        ProgressHelper.showDialog(getContext(), "");
        List<CartModel> listCheck = getListFood();
        List<String> listID = new ArrayList<>();
        List<CartModel> listPositionCheck = new ArrayList<>();
        for (int i = 0; i < listCheck.size(); i++) {
            if (listCheck.get(i).isCheck()) {
                listID.add(listCheck.get(i).getDocumentID());
                listPositionCheck.add(listCheck.get(i));
            }
        }
        for (CartModel i : listPositionCheck) {
            list.remove(i);
        }
        adapter.notifyDataSetChanged();
        if (listID.size() > 0) {
            WriteBatch batch = db.batch();
            for (String documentId : listID) {
                DocumentReference documentReference = db.collection("cart").document(documentId);
                batch.delete(documentReference);
            }

            batch.commit().addOnSuccessListener(unused -> {
                ProgressHelper.dismissDialog();
            }).addOnFailureListener(e -> ProgressHelper.dismissDialog());
        }
    }


    private List<CartModel> getListFood() {
        List<CartModel> listCheck = new ArrayList<>();
        if (list.size() > 0) {
            list.forEach(cartModel -> {
                if (cartModel.isCheck()) {
                    listCheck.add(cartModel);
                }
            });
        }
        return listCheck;
    }

    @Override
    public void onClick(int position) {
        CartModel cartModel = list.get(position);
        if (cartModel != null) {
            new AlertDialog.Builder(getContext()).setMessage("Bạn có muốn xóa không ?").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    removeCart(cartModel, position);
                    setTotalPrice();
                }
            }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    private void removeCart(CartModel cartModel, int position) {
        if (cartModel != null) {
            ProgressHelper.showDialog(getContext(), "Đang xóa giỏ hàng");
            db.collection("cart").document(cartModel.getDocumentID()).delete().addOnCompleteListener(task -> {
                ProgressHelper.dismissDialog();
                list.remove(position);
                adapter.notifyItemRemoved(position);
            }).addOnFailureListener(e -> {
                ProgressHelper.dismissDialog();
            });
        }
    }

    private Long getTotalPrice() {
        long total = 0L;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isCheck()) {
                total += list.get(i).getPrice() * list.get(i).getAmount();
            }
        }
        return total;
    }

    @Override
    public void setTotalPrice() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        String totalPrice = "Tổng tiền: " + formatter.format(getTotalPrice()) + " VNĐ";
        binding.tvTotal.setText(totalPrice);
        binding.tvSoLuongChon.setText("Đã chọn: " + getListFood().size());
    }
}