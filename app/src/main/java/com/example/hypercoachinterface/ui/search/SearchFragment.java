package com.example.hypercoachinterface.ui.search;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.databinding.FragmentSearchBinding;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.example.hypercoachinterface.ui.favorites.FavoritesViewModel;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private App app;
    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;
    private final List<RoutineSummary> dataSet = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        app = (App) getActivity().getApplication();

        ViewModelProvider.Factory viewModelFactory = new RepositoryViewModelFactory<>(RoutineRepository.class, app.getRoutineRepository());
        searchViewModel = new ViewModelProvider(this, viewModelFactory).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        for (int i = 1; i <= 50; i++)
            dataSet.add(new RoutineSummary(i, 0,"Routine " + i, null));

        ItemAdapter adapter = new ItemAdapter(dataSet);

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


        binding.filterRoutinesView.setLayoutManager(new GridLayoutManager(
                this.getContext(),
                columns,
                GridLayoutManager.VERTICAL,
                false));
        binding.filterRoutinesView.setAdapter(adapter);


        // TODO: Move out and save on destroy to avoid losing the filter
        TextInputLayout filter =  binding.filterBy;
        AutoCompleteTextView filterOptions = binding.filterOptions;
        TextInputLayout order = binding.orderBy;
        AutoCompleteTextView orderOptions = binding.orderOptions;

        ArrayList<String> orderOptionsDataSet = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.order_by_options)));
        ArrayList<String> filterOptionsDataSet = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.filter_by_options)));

        ArrayAdapter<String> orderArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item, orderOptionsDataSet);
        ArrayAdapter<String> filterArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item, filterOptionsDataSet);

        filterOptions.setAdapter(filterArrayAdapter);
        orderOptions.setAdapter(orderArrayAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}