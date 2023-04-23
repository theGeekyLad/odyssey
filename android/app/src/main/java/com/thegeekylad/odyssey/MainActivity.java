package com.thegeekylad.odyssey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.thegeekylad.odyssey.service.Api;
import com.thegeekylad.odyssey.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private Api api;
    private MaterialAutoCompleteTextView dropdownSourceStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view model
        MainActivityViewModel viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.showProgress.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
                else
                    ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
            }
        });

        // api calls
        api = Api.getInstance(this, viewModel);

        // init
        dropdownSourceStop = (MaterialAutoCompleteTextView) findViewById(R.id.menu_text_source_stop);

        // nav graph
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // bottom bar
        ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setOnItemSelectedListener(item -> {
            NavOptions navOptions;

            switch (item.getItemId()) {
                case R.id.menu_item_explore:
                    navOptions = new NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(navController.getCurrentDestination().getId(), true)
                            .build();
                    navController.navigate(R.id.exploreFragment, null, navOptions);
                    return true;
                case R.id.menu_item_destination:
                    navOptions = new NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(navController.getCurrentDestination().getId(), true)
                            .build();
                    navController.navigate(R.id.destinationFragment,null, navOptions);
                    return true;
                case R.id.menu_item_map:
                    navOptions = new NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(navController.getCurrentDestination().getId(), true)
                            .build();
                    navController.navigate(R.id.mapsFragment,null, navOptions);
                    return true;
                default: return false;
            }
        });
    }
}