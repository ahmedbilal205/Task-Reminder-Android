package com.anbdevelopers.simpletaskreminder.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import com.anbdevelopers.simpletaskreminder.R;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.anbdevelopers.simpletaskreminder.data.CompletedTaskClass;
import com.anbdevelopers.simpletaskreminder.data.DatabaseHelperComTask;
import com.anbdevelopers.simpletaskreminder.ui.SendToComplete;

import java.util.ArrayList;

public class CompletedTaskFragment extends Fragment {
    private DatabaseHelperComTask db;
    private ArrayList<CompletedTaskClass> completedListItem;
    private ListView CompletedtaskList;
    private Intent intent;
    //private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.compeleted_layout, container, false);
        //initialize banner ad
        //TODO implement ads
//        mAdView = v.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        getActivity ().setTitle ("Completed Task");

        RelativeLayout emptyView = v.findViewById (R.id.emptyView);

        CompletedtaskList = v.findViewById (R.id.CompletedListView);
        //set empty view for list view
        CompletedtaskList.setEmptyView (emptyView);
        completedListItem = new ArrayList<>();

        db = new DatabaseHelperComTask (getContext ());

        intent = getActivity ().getIntent ();

        if (intent.getExtras () != null) {
            CompletedTaskClass completedTaskClass = new CompletedTaskClass ();

            String title=intent.getStringExtra (SendToComplete.TITLE);
            String time=intent.getStringExtra (SendToComplete.TIME);

            if (title != null && !title.trim ().equals ("")){

                if(time != null && !time.trim ().equals ("")){
                    completedTaskClass.setTitle (title);
                    completedTaskClass.setTime (time);
                    db.insertData (completedTaskClass);
                    completedListItem.clear ();
                    DisplayData ();
                }
            }
        }


        return v;

    }

    private void DisplayData() {
        Cursor cursor = db.ViewData ();
        if (cursor.getCount () >= 1) {
            while (cursor.moveToNext ()) {
                String title = cursor.getString (1);
                String time = cursor.getString (2);
                CompletedTaskClass completedTaskClass = new CompletedTaskClass (title, time);
                completedListItem.add (completedTaskClass);
            }
            CompletedTaskadapter myTaskAdapter = new CompletedTaskadapter (getContext (), completedListItem);
            CompletedtaskList.setAdapter (myTaskAdapter);
            CompletedtaskList.setOnItemClickListener ((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder (getContext ());
                builder.setCancelable (false);
                builder.setTitle ("Choose option");
                builder.setMessage ("Delete " + completedListItem.get (position).getTitle () + "?");
                builder.setPositiveButton ("Delete", (dialog, which) -> {
                    db.deleteData (completedListItem.get (position).getTitle ());
                    completedListItem.remove (position);
                    myTaskAdapter.notifyDataSetChanged ();
                    Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();

                });
                builder.setNegativeButton ("Cancel", (dialog, which) -> {
                    dialog.dismiss ();
                    Toast.makeText(getContext(), "cancelled", Toast.LENGTH_SHORT).show();
                });
                builder.create ().show ();
            });
        }
        setHasOptionsMenu (true);
    }

    @Override
    public void onResume() {
        super.onResume ();
        completedListItem.clear ();
        DisplayData ();
        intent.removeExtra (SendToComplete.TITLE);
        intent.removeExtra (SendToComplete.TIME);

    }

    public class CompletedTaskadapter extends BaseAdapter {
        Context context;
        ArrayList<CompletedTaskClass> completedTask;

        public CompletedTaskadapter(Context c, ArrayList<CompletedTaskClass> completedTasks) {
            this.context = c;
            this.completedTask = completedTasks;

        }

        @Override
        public int getCount() {
            return completedTask.size ();
        }

        @Override
        public Object getItem(int position) {
            return completedTask.get (position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if (convertView == null) {

                convertView = LayoutInflater.from (context).inflate (R.layout.completedtaskrow, parent, false);
            }
            CompletedTaskClass taskClass = (CompletedTaskClass) getItem (position);

            TextView title = convertView.findViewById (R.id.completedTasKTitle);
            TextView time = convertView.findViewById (R.id.completedTasKTime);

            title.setText (taskClass.getTitle ());
            time.setText (taskClass.getTime ());

            return convertView;
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu (menu, inflater);
        inflater.inflate (R.menu.com_delete_all, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.delete_all:
                Cursor cursor = db.ViewData ();
                if (cursor.getCount () == 0) {
                    Toast.makeText(getContext(), "no task completed yet", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder (getContext ());
                    builder.setCancelable (false);
                    builder.setTitle ("Delete All Completed Tasks?");
                    builder.setMessage ("this operation cannot be undone when clicked, do you wish to continue?")
                            .setPositiveButton ("Yes", (dialog, which) -> {
                                db.deleteAllData ();
                                completedListItem.clear ();
                                ((BaseAdapter) CompletedtaskList.getAdapter ()).notifyDataSetChanged ();
                                DisplayData ();
                                Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();

                            });
                    builder.setNegativeButton ("No", (dialog, which) -> {
                        dialog.dismiss ();
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    });
                    builder.create ().show ();
                }
        }
        return super.onOptionsItemSelected (item);
    }
}
