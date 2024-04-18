package com.example.foodorder.foodorder;




import static com.example.foodorder.foodorder.ShowMessageHelper.showMessage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorder.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setOnClick();
    }

    private void setOnClick() {
        binding.btnSignUp.setOnClickListener(view -> {
            if (TextUtils.isEmpty(binding.edtUserName.getText())) {
                showMessage(this, "Không được để trống họ và tên");
            } else if (TextUtils.isEmpty(binding.edtFullName.getText())) {
                showMessage(this, "Không được để trống tài khoản");
            } else if (TextUtils.isEmpty(binding.edtPassword.getText())) {
                showMessage(this, "Không được để trống mật khẩu");
            } else if (TextUtils.isEmpty(binding.edtPhone.getText())) {
                showMessage(this, "Không được để trống số điện thoại");
            } else if (TextUtils.isEmpty(binding.edtAddress.getText())) {
                showMessage(this, "Không được để trống địa chỉ");
            } else {
                ProgressHelper.showDialog(this, "Đang đăng kí");
                db.collection("user").whereEqualTo("userName", binding.edtUserName.getText().toString())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    // userName đã tồn tại trong bảng "users"
                                    ProgressHelper.dismissDialog();
                                    showMessage(SignUpActivity.this, "Tên tài khoản đã tồn tại, Vui lòng nhập tài khoản khác");
                                } else {
                                    // userName chưa tồn tại trong bảng "users"
                                    SignUp();
                                }
                            }
                        });
            }
        });
    }

    private void SignUp() {
        Map<String, String> user = new HashMap<>();
        user.put("id", String.valueOf(System.currentTimeMillis()));
        user.put("role", "user");
        user.put("fullName", binding.edtFullName.getText().toString());
        user.put("userName", binding.edtUserName.getText().toString());
        user.put("password", binding.edtPassword.getText().toString());
        user.put("phone", binding.edtPhone.getText().toString());
        user.put("address", binding.edtAddress.getText().toString());
        db.collection("user").add(user).addOnSuccessListener(documentReference -> {
            showMessage(SignUpActivity.this, "Đăng kí thành công");
            ProgressHelper.dismissDialog();
            finish();
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        }).addOnFailureListener(e -> {
            showMessage(SignUpActivity.this, "Đăng kí thất bại");
            ProgressHelper.dismissDialog();
        });
    }

}