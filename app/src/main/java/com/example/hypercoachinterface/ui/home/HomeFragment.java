package com.example.hypercoachinterface.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hypercoachinterface.databinding.FragmentHomeBinding;
import com.example.hypercoachinterface.ui.home.adapter.ItemAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ArrayList<String> dataSet = new ArrayList<>();
    private ItemAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        for (int i = 1; i <= 50; i++)
            dataSet.add("Item " + i);

        adapter = new ItemAdapter(dataSet);

        binding.myRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.myRoutinesView.setAdapter(adapter);

        binding.allRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.allRoutinesView.setAdapter(adapter);

        binding.favouritesRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.favouritesRoutinesView.setAdapter(adapter);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                return;
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}