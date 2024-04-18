package com.example.foodorder.foodorder;




import static com.example.foodorder.foodorder.ShowMessageHelper.showMessage;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorder.databinding.ActivityLoginBinding;
import com.example.foodorder.model.UserData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setOnClick();
        SharedPref.init(getApplicationContext());
    }

    private void setOnClick() {
        binding.btnLogin.setOnClickListener(view -> {
            ProgressHelper.showDialog(this, "Đang đăng nhập");
            db.collection("user").whereEqualTo("userName", binding.edtUserName.getText().toString()).whereEqualTo("password", binding.edtPassword.getText().toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // userName đã tồn tại trong bảng "users"
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            UserData user = new UserData();
                            user.setFullName(document.getString("fullName"));
                            user.setUserName(document.getString("userName"));
                            user.setAddress(document.getString("address"));
                            user.setPassword(document.getString("password"));
                            user.setPhone(document.getString("phone"));
                            user.setRole(document.getString("role"));
                            user.setDocumentID(document.getId());
                            SharedPref.write(SharedPref.USER_DATA, new Gson().toJson(user));
                        }
                        finish();
                        ProgressHelper.dismissDialog();
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        // userName chưa tồn tại trong bảng "users"
                        ProgressHelper.dismissDialog();
                        showMessage(LoginActivity.this, "Tên tài khoản hoặc mật khẩu không chính xác");

                    }
                }
            });

        });
        binding.btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

    }
}