package com.anbdevelopers.simpletaskreminder.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anbdevelopers.simpletaskreminder.R;
import com.anbdevelopers.simpletaskreminder.ui.AboutAppActivity;


public class ProfileFragment extends Fragment {
    RelativeLayout aboutRelLayout;
    RelativeLayout shareRelLayout;
    RelativeLayout rateRelLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.profile_layout, container, false);
        getActivity ().setTitle ("My Profile");

        aboutRelLayout = v.findViewById(R.id.about_rel_layout);
        shareRelLayout = v.findViewById(R.id.share_rel_layout);
        rateRelLayout = v.findViewById(R.id.rate_rel_layout);


        aboutRelLayout.setOnClickListener (v1 -> {
            //TODO uncomment and add about activity
            Intent intent = new Intent (this.getContext (), AboutAppActivity.class);//start about app activity
            startActivity (intent);

        });

        //rate app function, this will come in the update
        rateRelLayout.setOnClickListener (v16 -> {
            rateApp();
        });

        //share function
        shareRelLayout.setOnClickListener (v14 -> {
            shareApp();
        });

        return v;
    }


    private void shareApp() {
        //TODO remember to replace the google play with portfolio website link
        Intent a = new Intent (Intent.ACTION_SEND);
        final String appPackageName = getActivity ().getApplicationContext ().getPackageName ();
        String strAppLink;
        try {
            strAppLink = "https://play.google.com/store/apps/details?id" + appPackageName;
        } catch (ActivityNotFoundException e) {
            strAppLink = "https://play.google.com/store/apps/details?id" + appPackageName;
        }
        a.setType ("text/link");
        String shareBody = "Hey, Check out Task-it, i use it to manage my Todo's. Get it for free at " + "\n" + "" + strAppLink;
        String shareSub = "APP NAME/TITLE";
        a.putExtra (Intent.EXTRA_SUBJECT, shareSub);
        a.putExtra (Intent.EXTRA_TEXT, shareBody);
        startActivity (Intent.createChooser (a, "Share Using"));
    }

    private void rateApp() {
        try{
            startActivity (new Intent (Intent.ACTION_VIEW,
                    Uri.parse ("market://details?id=" +getContext ().getPackageName ())));
        }catch (ActivityNotFoundException e){
            startActivity (new Intent (Intent.ACTION_VIEW,
                    Uri.parse ("http://play.google.com/store/apps/details?id=" +getContext ().getPackageName ())));
        }

    }
}
