package com.example.foodorder.foodorder;

import android.content.Context;
import android.widget.Toast;

public class ShowMessageHelper {
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
