package com.anbdevelopers.simpletaskreminder.ui;

import android.app.FragmentManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anbdevelopers.simpletaskreminder.R;
import com.anbdevelopers.simpletaskreminder.fragments.CompletedTaskFragment;
import com.anbdevelopers.simpletaskreminder.fragments.HomeFragment;
import com.anbdevelopers.simpletaskreminder.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById (R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //this checks to see if there's any savedInstance,if null, then it replaces the fragment container
        // with home fragment.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,
                    new HomeFragment ()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener=
            menuItem -> {
                Fragment selectedfragment=null;
                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        selectedfragment=new HomeFragment();
                        break;

                    case R.id.completed:
                       // Toast.makeText(this, "Compl", Toast.LENGTH_SHORT).show();
                       selectedfragment=new CompletedTaskFragment();
                        break;
                    case R.id.profile:
                        selectedfragment=new ProfileFragment();
                        break;
                }
                if (selectedfragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,
                            selectedfragment).commit();
                }

                return true;
            };

    @Override
    public void onBackPressed() {
        FragmentManager fm=getFragmentManager ();
        if (fm.getBackStackEntryCount ()>0) {
            fm.popBackStack ();
        }
        else {
            super.onBackPressed ();
        }
    }
}