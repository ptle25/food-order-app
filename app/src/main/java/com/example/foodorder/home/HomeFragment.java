package com.example.foodorder.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;


import com.example.foodorder.databinding.FragmentHomeBinding;
import com.example.foodorder.foodorder.DetailFoodActivity;
import com.example.foodorder.foodorder.ProgressHelper;
import com.example.foodorder.foodorder.SharedPref;
import com.example.foodorder.foodorder.ShowMessageHelper;
import com.example.foodorder.foodorder.ThemSanPhamActivity;
import com.example.foodorder.model.FoodModel;
import com.example.foodorder.model.UserData;
import com.example.foodorder.updatefood.UpdateFoodActivity;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.youth.banner.indicator.CircleIndicator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment implements HomeAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    List<FoodModel> list = new ArrayList<>();
    private HomeAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData userData;

    private ImageAdapter imageAdapter;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userData = new Gson().fromJson(SharedPref.read(SharedPref.USER_DATA, ""), UserData.class);

        if (userData != null && !TextUtils.isEmpty(userData.getRole()) && userData.getRole().equals("admin")) {
            binding.imgAddFood.setVisibility(View.VISIBLE);
            binding.imgAddFood.setOnClickListener(v -> startActivity(new Intent(getContext(), ThemSanPhamActivity.class)));
        }
        imageAdapter = new ImageAdapter(getBanner());
        binding.banner.setAdapter(imageAdapter);
        binding.banner.addBannerLifecycleObserver(this).setIndicator(new CircleIndicator(getContext()));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    adapter.updateList(list);
                } else {
                    filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    adapter.updateList(list);
                } else {
                    filter(newText);
                }
                return true;
            }
        });

//        ImageView clearButton = binding.searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
//        clearButton.setOnClickListener(v -> {
//            binding.searchView.setQuery("", false);
//            adapter.updateList(this.list);
//        });
    }

    private List<String> getBanner() {
        List<String> banner = new ArrayList<>();
        banner.add("https://dulichvietnam.com.vn/vnt_upload/File/Image/50_quy_tac_tren_mam_com_nguoi_Viet_7.jpg");
        banner.add("https://barona.vn/storage/uu-dai/barona-uu-dai-freeship-2.png");
        banner.add("https://vietnam.travel/sites/default/files/inline-images/vietnamese%20street%20food-3.jpg");
        banner.add("https://lh3.googleusercontent.com/proxy/S4y5QQpHmWVndnomPfUJRArDw2FBHflRPpbNIzCVVgHpOI8EwTfEo4gGAPYQNnqbtCO3HmvvnGOzemLYmbms0VjTdfQe3LkGg9608qGr7puR");
        banner.add("https://scontent.fhan7-1.fna.fbcdn.net/v/t39.30808-6/347826075_255851763615298_790358806878313883_n.jpg?_nc_cat=106&ccb=1-7&_nc_sid=5f2048&_nc_ohc=LVLUQFWjogAAb6B9ZFK&_nc_ht=scontent.fhan7-1.fna&oh=00_AfDniLd4xD-uOHvMcQPDPnh1l0pjY2K5qr9bboATiL2X8g&oe=66268B55");
        return banner;
    }

    private void getAllFoods() {
        list.clear();
        ProgressHelper.showDialog(getContext(), "Đang lấy dữ liệu");
        db.collection("foods").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ProgressHelper.dismissDialog();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FoodModel foodModel = new FoodModel();
                                foodModel.setDocumentID(document.getId());
                                foodModel.setImage(document.getString("image"));
                                foodModel.setName(document.getString("name"));
                                foodModel.setDescription(document.getString("description"));
                                foodModel.setPrice(document.getLong("price"));
                                list.add(foodModel);
                            }
                            adapter = new HomeAdapter(list);
                            adapter.setOnItemClickListener(HomeFragment.this);
                            binding.recyclerView.setAdapter(adapter);
                            binding.recyclerView.setHasFixedSize(true);
                        }
                    }
                });
    }

    @Override
    public void onClick(int position) {
        if (userData != null && !TextUtils.isEmpty(userData.getRole()) && userData.getRole().equals("admin")) {
            Intent intent = new Intent(requireContext(), UpdateFoodActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("documentID", list.get(position).getDocumentID());
            bundle.putSerializable("data", adapter.getList().get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), DetailFoodActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", adapter.getList().get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onDelete(int position) {
        if (userData != null && !TextUtils.isEmpty(userData.getRole()) && userData.getRole().equals("admin")) {
            new AlertDialog.Builder(requireContext()).setMessage("Bạn có muốn xóa không ?").setPositiveButton("Có", (dialog, which) ->
                    deleteFood(position)).setNegativeButton("Không", (dialog, which) -> {
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    private void deleteFood(int position) {
        ProgressHelper.showDialog(getContext(), "Đang xóa");
        db.collection("foods").document(list.get(position).getDocumentID()).delete().addOnSuccessListener(unused -> {
            ProgressHelper.dismissDialog();
            ShowMessageHelper.showMessage(getContext(), "Xóa thành công");
            list.remove(position);
            adapter.notifyItemRemoved(position);
        }).addOnFailureListener(e -> ProgressHelper.dismissDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllFoods();
    }


    void filter(String text) {
        List<FoodModel> temp = new ArrayList<>();
        for (FoodModel d : list) {
            if (removeAccent(d.getName().toLowerCase().trim()).contains(removeAccent(text).toLowerCase().trim())) {
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }

    public String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp;
    }
}
