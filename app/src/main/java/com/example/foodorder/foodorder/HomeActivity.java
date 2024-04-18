package com.example.foodorder.foodorder;

import static androidx.navigation.ui.BottomNavigationViewKt.setupWithNavController;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.example.foodorder.R;
import com.example.foodorder.databinding.ActivityHomeBinding;
import com.example.foodorder.model.UserData;
import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity {

    private UserData userData;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUp();
        setOnClick();

    }

    private void setUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        setupWithNavController(binding.bottomNavigation, navHostFragment.getNavController());
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);
        if (userData != null && userData.getRole() != null && userData.getRole().equals("admin")) {
            binding.bottomNavigation.getMenu().findItem(R.id.cartFragment).setVisible(false);
            binding.bottomNavigation.getMenu().findItem(R.id.orderHistoryFragment).setVisible(false);
        }
        if (userData != null && userData.getRole() != null && userData.getRole().equals("user")) {
            binding.bottomNavigation.getMenu().findItem(R.id.allOrderFragment).setVisible(false);
        }
    }

    private void setOnClick() {

    }
}