package com.example.foodorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodorder.cart.CartAdapter;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.foodorder.SharedPref;
import com.example.foodorder.foodorder.ShowMessageHelper;
import com.example.foodorder.model.CartModel;
import com.example.foodorder.model.OrderModel;
import com.example.foodorder.model.UserData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChiTietDatHangActivity extends AppCompatActivity {
    private UserData userData;
    List<CartModel> list;
    private TextView hoten;
    private TextView diachi;
    private TextView sodienthoai;
    private RecyclerView recyclerView;
    private Button btnOrder;
    private CartAdapter cartAdapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_dat_hang);
        initView();
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);
        hoten.setText("Họ tên: " + userData.getFullName());
        diachi.setText("Địa chỉ: " + userData.getAddress());
        sodienthoai.setText("Số điện thoại: " + userData.getPhone());
        list = (List<CartModel>) getIntent().getExtras().get("data");
        cartAdapter = new CartAdapter(list);
        recyclerView.setAdapter(cartAdapter);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order();
            }
        });
    }

    private void initView() {
        hoten = findViewById(R.id.hoten);
        diachi = findViewById(R.id.diachi);
        sodienthoai = findViewById(R.id.sodienthoai);
        recyclerView = findViewById(R.id.recyclerView);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void order() {
        ProgressHelper.showDialog(this, "Đang order");
        OrderModel orderModel = new OrderModel();
        orderModel.setDateOrder(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        orderModel.setUserID(userData.getDocumentID());
        orderModel.setFoods(getListFood());
        db.collection("order").add(orderModel).addOnCompleteListener(task -> {
            ProgressHelper.dismissDialog();
            ShowMessageHelper.showMessage(this, "Order thành công");
            removeOrder();
            this.finish();
        }).addOnFailureListener(e -> ProgressHelper.dismissDialog());
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

    private void removeOrder() {
        ProgressHelper.showDialog(this, "");
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
        cartAdapter.notifyDataSetChanged();
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
}