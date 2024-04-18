package com.example.foodorder.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import com.example.foodorder.R;
import com.example.foodorder.databinding.FragmentProfileBinding;
import com.example.foodorder.foodorder.LoginActivity;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.foodorder.SharedPref;
import com.example.foodorder.foodorder.ShowMessageHelper;
import com.example.foodorder.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData userData;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);

        if (userData != null) {
            binding.accountName.setText("Tài khoản: " + userData.getUserName());
            binding.address.setText("Địa chỉ: " + userData.getAddress());
            binding.phone.setText("Số điện thoại: " + userData.getPhone() + "");
            binding.fullName.setText("Họ và tên: " + userData.getFullName());
        }
        binding.changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(requireContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_layout_alert, null);
                Button button = dialogView.findViewById(R.id.btnDoiMK);
                TextInputEditText edtNewPass = dialogView.findViewById(R.id.edtNewPass);
                TextInputEditText edtOldPass = dialogView.findViewById(R.id.edtPassOld);
                TextInputEditText edtConfirmNewPass = dialogView.findViewById(R.id.edtConfirmNewPass);
                button.setOnClickListener(v1 -> {
                    updatePassword(Objects.requireNonNull(edtOldPass.getText()).toString(), Objects.requireNonNull(edtNewPass.getText()).toString(), Objects.requireNonNull(edtConfirmNewPass.getText()).toString());
                });
                dialogBuilder.setView(dialogView);
                alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updatePassword(String oldPass, String newPass, String confirmPassNew) {
        if (oldPass.equals("") || newPass.equals("") || confirmPassNew.equals("")) {
            ShowMessageHelper.showMessage(getContext(), "Chưa nhập đủ mật khẩu");
        } else if (!oldPass.equals(userData.getPassword())) {
            ShowMessageHelper.showMessage(getContext(), "Mật khẩu cũ chưa đúng");
        } else if (!newPass.equals(confirmPassNew)) {
            ShowMessageHelper.showMessage(getContext(), "Mật khẩu nhắc lại chưa đúng");
        } else {
            ProgressHelper.showDialog(getContext(), "Đang đổi mật khẩu");
            db.collection("user").document(userData.getDocumentID())
                    .update("password", newPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ProgressHelper.dismissDialog();
                            ShowMessageHelper.showMessage(getContext(), "Đổi mật khẩu thành công");
                            alertDialog.dismiss();
                        }
                    }).addOnFailureListener(e -> ProgressHelper.dismissDialog());
        }

    }
}