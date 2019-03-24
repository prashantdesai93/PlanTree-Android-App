package com.ideas2app.plantree;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

public class AddConcernActivity extends AppCompatActivity /*implements OnMapReadyCallback*/ {

   // private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    Spinner spinner;
    EditText etConcernTitle, etConcernDescription;
    static  EditText etConcernDate;
    //static EditText etEventDate, etEventStartTime, etEventEndTime;
    String concernTitleValue, concernDescValue, dropDownValue, concernDateValue;
    Button btnEventAdd, btnEventCancel;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concern);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        etConcernTitle = findViewById(R.id.etConcernTitle);
        etConcernDescription = findViewById(R.id.etConcerntDescription);
        etConcernDate = findViewById(R.id.etConcernDate);
//        etEventDate = findViewById(R.id.etEventDate);
//        etEventStartTime = findViewById(R.id.etEventStartTime);
//        etEventEndTime = findViewById(R.id.etEventEndTime);
//        etEventPlace = findViewById(R.id.etEventPlace);

        spinner = (Spinner) findViewById(R.id.spinner);
        String[] countryArray = {"Character", "Environment", "Engagement", "Housing", "Transportation", "Economy", "Education", "Health", "Safety"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,R.id.textViewss, countryArray);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dropDownValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etConcernDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

//        etEventStartTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new StartTimePickerFragment();
//                newFragment.show(getFragmentManager(), "TimePicker");
//            }
//        });
//        etEventEndTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new EndTimePickerFragment();
//                newFragment.show(getFragmentManager(), "TimePicker");
//            }
//        });

        btnEventAdd = findViewById(R.id.btnAddEvent);
        btnEventCancel = findViewById(R.id.btnEventCancel);

        btnEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEventAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error=false;
                try{
                    if(etConcernTitle.getText().length() > 1){
                        concernTitleValue = etConcernTitle.getText().toString();
                    } else{
                        error = true;
                        etConcernTitle.setError("Required");
                    }

                    if(etConcernDescription.getText().length() > 1){
                        concernDescValue = etConcernDescription.getText().toString();
                    } else{
                        error = true;
                        etConcernDescription.setError("Required");
                    }

                    if(etConcernDate.getText().length() > 1){
                        concernDateValue = etConcernDate.getText().toString();
                    } else{
                        error = true;
                        etConcernDate.setError("Required");
                    }

//                    if(etEventStartTime.getText().length() > 1){
//                        eventStartTimeValue = etEventStartTime.getText().toString();
//                    } else{
//                        error = true;
//                        etEventStartTime.setError("Required");
//                    }
//
//                    if(etEventEndTime.getText().length() > 1){
//                        eventEndTimeValue = etEventEndTime.getText().toString();
//                    } else{
//                        error = true;
//                        etEventEndTime.setError("Required");
//                    }
//
//                    if(etEventPlace.getText().length() > 1){
//                        eventPlaceValue = etEventPlace.getText().toString();
//                    } else{
//                        error = true;
//                        etEventPlace.setError("Required");
//                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

                if(!error){
                    if(isConnected()){
                        progressDialog = new ProgressDialog(AddConcernActivity.this);
                        progressDialog.getWindow().setBackgroundDrawable(new
                                ColorDrawable(android.graphics.Color.TRANSPARENT));
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.progressdialog);

                        Concern concern = new Concern();
                        concern.cTitle = concernTitleValue;
                        concern.cDescription = concernDescValue;
                        concern.cDate = concernDateValue;
                        concern.cCategory = dropDownValue;
                        concern.cLat = "35.229223";
                        concern.cLng = "-80.839688";
                        concern.cUpVotes = "0";
                        concern.cDownVotes = "0";

                        addEvent(concern);
                        Intent i = new Intent(AddConcernActivity.this, DashboardActivity.class);
                        finish();
                        startActivity(i);
                    } else{
                        Toast.makeText(AddConcernActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddConcernActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void addEvent(Concern event){
        String key = myRef.child("allConcerns").push().getKey();
        event.cId= key;
        Map<String, Object> threadValues = event.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, threadValues);
        myRef.child("allConcerns").updateChildren(childUpdates);
        Toast.makeText(AddConcernActivity.this, "Concern Added", Toast.LENGTH_SHORT).show();
    }

//    public static class StartTimePickerFragment extends DialogFragment
//            implements TimePickerDialog.OnTimeSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//
//            return new TimePickerDialog(getActivity(), this, hour, minute,
//                    DateFormat.is24HourFormat(getActivity()));
//        }
//
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            String amPM = (hourOfDay>12)?"PM":"AM";
//            hourOfDay = hourOfDay % 12;
//            etEventStartTime.setText(hourOfDay+ ":"+minute+" "+amPM);
//        }
//    }
//
//    public static class EndTimePickerFragment extends DialogFragment
//            implements TimePickerDialog.OnTimeSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);
//
//            return new TimePickerDialog(getActivity(), this, hour, minute,
//                    DateFormat.is24HourFormat(getActivity()));
//        }
//
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            String amPM = (hourOfDay>12)?"PM":"AM";
//            hourOfDay = hourOfDay % 12;
//            etEventEndTime.setText(hourOfDay+ ":"+minute+" "+amPM);
//        }
//    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            etConcernDate.setText(month+1+"/"+day+"/"+year);
        }
    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        LatLng sydney = new LatLng(35.2290, -80.8398);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//    }
}
