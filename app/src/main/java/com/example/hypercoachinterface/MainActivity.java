package com.example.hypercoachinterface;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.AppPreferences;
import com.example.hypercoachinterface.backend.api.model.Exercise;
import com.example.hypercoachinterface.backend.api.model.Review;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityMainBinding;
import com.example.hypercoachinterface.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppPreferences preferences;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (App) getApplication();
        preferences = app.getPreferences();

        if(preferences.getAuthToken() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.bottom_nav_menu);
        NavigationView landNavView = findViewById(R.id.land_nav_menu);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search,
                R.id.navigation_profile, R.id.navigation_favorites)
                .build();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (binding.bottomNavMenu != null) NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);
        if (binding.landNavMenu != null) NavigationUI.setupWithNavController(binding.landNavMenu, navController);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this, "landscape", Toast.LENGTH_LONG).show();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView recyclerView = findViewById(R.id.all_favourites_routines_view);

            int columns = 0;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.heightPixels - R.dimen.nav_menu_width;

            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

            while (screenWidth > 0) {
                columns++;
                screenWidth -= R.dimen.routine_card_width;
            }

            recyclerView.setLayoutManager(new GridLayoutManager(
                    getApplicationContext(),
                    columns,
                    GridLayoutManager.VERTICAL,
                    false));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            recreate();
        }
    }

}