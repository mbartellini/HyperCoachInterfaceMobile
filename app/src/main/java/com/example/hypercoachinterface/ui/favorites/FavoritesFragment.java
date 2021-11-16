package com.example.hypercoachinterface.ui.favorites;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.databinding.FragmentFavoritesBinding;
import com.example.hypercoachinterface.databinding.FragmentHomeBinding;
import com.example.hypercoachinterface.ui.favorites.adapter.FavItemAdapter;
import com.example.hypercoachinterface.ui.home.HomeViewModel;
import com.example.hypercoachinterface.ui.home.adapter.ItemAdapter;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel favoritesViewModel;
    private FragmentFavoritesBinding binding;
    private ArrayList<String> dataSet = new ArrayList<>();
    private FavItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        for (int i = 1; i <= 50; i++)
            dataSet.add("Item " + i);

        adapter = new FavItemAdapter(dataSet);

        int columns = 0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        FragmentActivity activity = getActivity();
        if (activity != null) activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        else throw new IllegalStateException("activity in favourites is null");
        float screenWidth = displayMetrics.widthPixels;
        float navMenuWidth = getResources().getDimensionPixelSize(R.dimen.nav_menu_width);
        float routineCardWidth = getResources().getDimensionPixelSize(R.dimen.routine_card_width);
        float routineCardMargin = getResources().getDimensionPixelSize(R.dimen.margin_medium);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            screenWidth = screenWidth - navMenuWidth;

        while (screenWidth > routineCardWidth + (routineCardMargin * 2)) {
            columns++;
            screenWidth -= routineCardWidth + (routineCardMargin * 2);
        }

        if (columns == 0) {
            throw new IllegalStateException("Invalid screen size");
        }

        binding.allFavouritesRoutinesView.setLayoutManager(new GridLayoutManager(
                this.getContext(),
                columns,
                GridLayoutManager.VERTICAL,
                false));
        binding.allFavouritesRoutinesView.setAdapter(adapter);

        favoritesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
