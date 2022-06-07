package com.anbdevelopers.simpletaskreminder.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.anbdevelopers.simpletaskreminder.R;
import com.anbdevelopers.simpletaskreminder.receiver.AlertReceiver;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_ID = "com.anbdevelopers.simpletaskreminder.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.anbdevelopers.simpletaskreminder.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.anbdevelopers.simpletaskreminder.EXTRA_DES";
    public static final String EXTRA_TIME = "com.anbdevelopers.simpletaskreminder.EXTRA_TIME";

    private EditText titleTask;
    private EditText Description;
    private TextView TaskTime;
    public int day, month, year, hour, minute;
    Calendar selectedDate;
    Button showPicker, cancelTask, saveTask;
    String dateSelected, currentTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_create_task);
        titleTask = findViewById (R.id.title_Task);
        Description = findViewById (R.id.task_Des);
        TaskTime = findViewById (R.id.task_time);
        saveTask = findViewById (R.id.SaveTask);
        cancelTask = findViewById (R.id.Cancel_Task);
        showPicker = findViewById (R.id.TimePicker);
        Intent intent = getIntent ();
        //get intent extras
        if (intent.hasExtra (EXTRA_ID)) {
            titleTask.setText (intent.getStringExtra (EXTRA_TITLE));
            Description.setText (intent.getStringExtra (EXTRA_DESC));
            TaskTime.setText (intent.getStringExtra (EXTRA_TIME));
            saveTask.setText (R.string.Task);
            cancelTask.setText (R.string.CancelUpdate);
            setTitle ("Edit Task");
        } else {
            setTitle ("Create Task");
        }
        //show date and time dialog
        showPicker.setOnClickListener (v -> {

            //check if the activity has an id
            if (intent.hasExtra (EXTRA_ID)) {
                //get text from tasktime textview
                String input = TaskTime.getText ().toString ();
                //check if user's default time is in 24 hours format
                if(is24hoursFormat (this)){
                    SimpleDateFormat sdf=new SimpleDateFormat ("MM/dd/yy,HH:mm", Locale.getDefault ());
                    try {
                        //format the text to a date
                        Date date = sdf.parse (input);
                        final Calendar calendar = Calendar.getInstance ();
                        assert date != null;
                        //set the date
                        calendar.setTime (date);
                        year = calendar.get (Calendar.YEAR);
                        month = calendar.get (Calendar.MONTH);
                        day = calendar.get (Calendar.DAY_OF_MONTH);
                        hour = calendar.get (Calendar.HOUR_OF_DAY);
                        minute = calendar.get (Calendar.MINUTE);


                    } catch (ParseException e) {
                        e.printStackTrace ();
                    }

                }
                else{
                    SimpleDateFormat sdf = new SimpleDateFormat ("MM/dd/yy,hh:mm aa", Locale.getDefault ());
                    try {
                        //format the text to a date
                        Date date = sdf.parse (input);
                        final Calendar calendar = Calendar.getInstance ();
                        assert date != null;
                        //set the date
                        calendar.setTime (date);
                        year = calendar.get (Calendar.YEAR);
                        month = calendar.get (Calendar.MONTH);
                        day = calendar.get (Calendar.DAY_OF_MONTH);
                        hour = calendar.get (Calendar.HOUR_OF_DAY);
                        minute = calendar.get (Calendar.MINUTE);


                    } catch (ParseException e) {
                        e.printStackTrace ();
                    }
                }

            } else {
                Calendar instance = Calendar.getInstance ();
                year = instance.get (Calendar.YEAR);
                month = instance.get (Calendar.MONTH);
                day = instance.get (Calendar.DAY_OF_MONTH);
                hour = instance.get (Calendar.HOUR_OF_DAY);
                minute = instance.get (Calendar.MINUTE);

            }


            //create an object of datepickerdialog class
            DatePickerDialog datePickerDialog = new DatePickerDialog (CreateTaskActivity.this,
                    CreateTaskActivity.this, year, month, day);

            //disable past dates
            datePickerDialog.getDatePicker ().setMinDate (System.currentTimeMillis () - 1000);


            //show datepicker
            datePickerDialog.show ();
        });


        saveTask.setOnClickListener (v -> CreateTaskActivity.this.SaveCreatedTask (intent));

        cancelTask.setOnClickListener (v -> {
            Intent a = new Intent (CreateTaskActivity.this, MainActivity.class);
            CreateTaskActivity.this.startActivity (a);
        });

    }

    private void SaveCreatedTask(Intent intent) {
        String title = titleTask.getText ().toString ();
        String description = Description.getText ().toString ();
        String time;
        // if the activity has an id
        if (intent.hasExtra (EXTRA_ID)) {
            //get text from taskTime textview
            time = TaskTime.getText ().toString ();
        } else {
            time = String.format ("%s,%s", dateSelected, currentTime);
        }


        if (TextUtils.isEmpty (title)) {
            titleTask.setError ("please input a Task");
            return;
        }

        if (TextUtils.isEmpty (description)) {
            Description.setError ("write a short description");
            return;
        }
        if(TaskTime.getText ().length ()<=1){
            Toast.makeText(this, "please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent ();
        data.putExtra (EXTRA_TITLE, title);
        data.putExtra (EXTRA_DESC, description);
        data.putExtra (EXTRA_TIME, time);


        int id = getIntent ().getIntExtra (EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra (EXTRA_ID, id);
        }
        setResult (RESULT_OK, data);
        TaskTime.setText (time);
        finish ();


    }

    private void fireNotification(Calendar targetCal) {
        Intent intent = new Intent (this, AlertReceiver.class);
        String title = titleTask.getText ().toString ();
        String des = Description.getText ().toString ();
        String time = String.format ("%s,%s", dateSelected, currentTime);


        intent.putExtra (EXTRA_TITLE, title);
        intent.putExtra (EXTRA_DESC, des);
        intent.putExtra (EXTRA_TIME, time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast (this,
                AlertReceiver.getID (),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService (ALARM_SERVICE);
        // start alarm if the selected time is not before the current time
        if (!targetCal.before (Calendar.getInstance ())) {
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,targetCal.getTimeInMillis(),pendingIntent);
                }else
                {
                    alarmManager.setExact (AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis (), pendingIntent);
                }
            }
        } else {
            //else cancel its pendingAlarm
            assert alarmManager != null;
            alarmManager.cancel (pendingIntent);
        }
    }




    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar c = Calendar.getInstance ();
        c.set (year, month, dayOfMonth);

        selectedDate = c;

        dateSelected = java.text.DateFormat.getDateInstance (java.text.DateFormat.SHORT).format (selectedDate.getTime ());


        TimePickerDialog timePickerDialog = new TimePickerDialog (CreateTaskActivity.this,
                CreateTaskActivity.this, hour,
                minute, android.text.format.DateFormat.is24HourFormat (this));
        timePickerDialog.show ();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Calendar c = Calendar.getInstance ();

        selectedDate.set (c.get (Calendar.YEAR), selectedDate.get (Calendar.MONTH),
                selectedDate.get (Calendar.DAY_OF_MONTH), hourOfDay, minute);
        currentTime = java.text.DateFormat.getTimeInstance (java.text.DateFormat.SHORT).format (selectedDate.getTime ());
        //set time on textview
        String time = String.format ("%s,%s", dateSelected, currentTime);
        TaskTime.setText (time);
        // send notification on selected time/date
        fireNotification (selectedDate);
    }


    public static boolean isToday(Date d) {
        return DateUtils.isToday (d.getTime ());
    }

    public static boolean isTomorrow(Date d) {
        return DateUtils.isToday (d.getTime () - DateUtils.DAY_IN_MILLIS);
    }
    public  static boolean is24hoursFormat(Context context){
        return android.text.format.DateFormat.is24HourFormat (context.getApplicationContext ());
    }
}
