package com.example.hypercoachinterface;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hypercoachinterface.backend.AppPreferences;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hypercoachinterface.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
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

        if(preferences.getAuthToken() == null)
            startActivity(new Intent(this, LoginActivity.class));

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
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (binding.bottomNavMenu != null) NavigationUI.setupWithNavController(binding.bottomNavMenu, navController);
        if (binding.landNavMenu != null) NavigationUI.setupWithNavController(binding.landNavMenu, navController);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//            BottomNavigationView navigation = findViewById(R.id.bottom_nav_menu);
//            navigation.setRotation(90f);
//            // navigation.requestLayout();
//            BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
//            for (int i = 0; i < menuView.getChildCount(); i++) {
//                final View iconView = menuView.getChildAt(i);
//                iconView.setRotation(-90f);
//            }
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            recreate();
//        }
    }

}