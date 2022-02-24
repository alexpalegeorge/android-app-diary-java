package com.example.paindiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.paindiary.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseUser;
import com.mapbox.mapboxsdk.geometry.LatLng;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser user;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Initialize user location to Monash Clayton
        latLng = new LatLng (-37.876823, 145.045837);

        setSupportActionBar(binding.appBar.toolbar);
        //Sets the action bar title to 'Home' rather than the default app name, when MainActivity is
        // first loaded
        getSupportActionBar().setTitle("@string/nav_home");

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragmentHome,
                R.id.fragmentEntry,
                R.id.fragmentRecord,
                R.id.fragmentReports,
                R.id.fragmentMap)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        //Sets up a NavigationView for use with a NavController
        NavigationUI.setupWithNavController(binding.navView, navController);
        //Sets up a toolbar for use with a NavController();
        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController,
                mAppBarConfiguration);

        //receive Firebase user from login activity
        final Intent intent = getIntent();
        user = intent.getParcelableExtra("userParcel");


    }

    public LatLng getLatLng() {return latLng;};

    public String getUserEmail() {
        return user.getEmail();
    }

    public void setLatLng(LatLng newLatLng) {
        latLng = newLatLng;
    }
}