package com.example.hypercoachinterface.ui.search;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.FragmentSearchBinding;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.example.hypercoachinterface.ui.favorites.FavoritesViewModel;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private App app;
    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;
    private final List<RoutineSummary> dataSet = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        app = (App) requireActivity().getApplication();

        searchViewModel = new ViewModelProvider(this,
                new SearchViewModel.SearchViewModelFactory(app.getRoutineRepository(), app.getCategoryRepository())).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* Horizontal or Vertical Layout Management */
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

        /* Fetching categories from API */
        searchViewModel.getCategories().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                for (RoutineCategory routineCategory : r.getData()) {
                    // DO SOMEHTING
                    // filter by: categoryId, difficulty
                    // order by: name, date, difficulty, favs
                }
            }
        });

        /* Fetching routines from API */
        ItemAdapter adapter = new ItemAdapter(dataSet);

        searchViewModel.getSearches().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                dataSet.clear();
                if (r.getData() != null) {
                    for(Routine routine : r.getData()) {
                        dataSet.add(RoutineSummary.fromRoutine(routine, 0));
                    }
                    adapter.notifyItemRangeChanged(dataSet.size() - r.getData().size() - 1, r.getData().size());
                }
            }
        });

        binding.filterRoutinesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (! binding.filterRoutinesView.canScrollVertically(1)) {
                    Log.d(TAG, "onScrollStateChanged: Reached bottom of scrollview, requesting more routines");
                    searchViewModel.getMoreSearches();
                    Log.d(TAG, "onScrollStateChanged: there are " + dataSet.size() + " routines");
                }
            }
        });

        binding.filterRoutinesView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}