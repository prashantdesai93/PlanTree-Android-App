package com.ideas2app.plantree;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    EditText etFirstName, etLastName, etEmail, etPasswordSignUp1, etPasswordSignUp2;
    static EditText etDate;
    //Spinner spinner;
    Button btnSignup, btnCancel;
    String universityValue, androidDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(SignUpActivity.this);
        setContentView(R.layout.activity_sign_up);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etDate = findViewById(R.id.etDate);
        etPasswordSignUp1 = (EditText) findViewById(R.id.etPasswordSignUp1);
        etPasswordSignUp2 = (EditText) findViewById(R.id.etPasswordSignUp2);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSignup = (Button) findViewById(R.id.btnSignUpSignScreen);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        if(getIntent() != null && getIntent().getExtras() != null ){
            universityValue = getIntent().getExtras().getString("universityValue");
        }

        //spinner = (Spinner) findViewById(R.id.spinner);
        //String[] countryArray = {"UNC Charlotte", "UNC Chapel-Hill", "University of Texas Dallas", "Northeastern University", "Carnegie Mellon University"};
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,R.id.textViewss, countryArray);
        //adapter.setDropDownViewResource(R.layout.spinner_item);
        //spinner.setAdapter(adapter);
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dropDownValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                finish();
                startActivity(i);
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User u = new User();
                String pass = null;
                String cPass = null;
                boolean error=false;
                try{
                    if(etEmail.getText().length()>1){
                        //u.setUser_email(etEmail.getText().toString());
                    }else{
                        error = true;
                        etEmail.setError("Required");
                    }
                    if(etFirstName.getText().length()>1){
                        //u.setUser_fname(etFname.getText().toString());
                    }else{
                        error = true;
                        etFirstName.setError("Required");
                    }
                    if(etLastName.getText().length()>1){
                        //u.setUser_lname(etLname.getText().toString());
                    }else{
                        error = true;
                        etLastName.setError("Required");
                    }
                    if (etDate.getText() != null && etDate.getText().length() > 0) {
                        //dateValue =Tasks.dtFormat.parse(etDate.getText().toString());
                        Log.d("demo: Date value",etDate.getText().toString());
                    } else {
                        etDate.setError("Required");
                        throw new Exception("Required");
                    }
                    if(etPasswordSignUp1.getText().length()>1){
                        pass =etPasswordSignUp1.getText().toString();
                    }else{
                        error = true;
                        etPasswordSignUp1.setError("Required");
                    }
                    if(etPasswordSignUp2.getText().length()>1){
                        cPass =etPasswordSignUp2.getText().toString();
                    }else{
                        error = true;
                        etPasswordSignUp2.setError("Required");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(!error){
                    if(pass.equalsIgnoreCase(cPass)){
                        if(isConnected()){
                            progressDialog = new ProgressDialog(SignUpActivity.this);
                            progressDialog.getWindow().setBackgroundDrawable(new
                                    ColorDrawable(android.graphics.Color.TRANSPARENT));
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.progressdialog);
                            createAccount(etEmail.getText().toString(),etPasswordSignUp1.getText().toString());
                        } else{
                            Toast.makeText(SignUpActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        etPasswordSignUp2.setError("Passwords Do not Match");
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createAccount(String email, String password) {
        Log.d("demo", "createAccount:" + email);

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "createUserWithEmail:success "+mAuth.getCurrentUser());
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, LoginActivity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean("hasLoggedIn", true);
                            editor.commit();
                            updateUserProfile();
                            // getMessageThreads();


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // [END create_user_with_email]
    }

    public  void updateUserProfile(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(etFirstName.getText().toString()+" "+etLastName.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "User profile updated.");
                            storeUser(user);
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

    public void storeUser(FirebaseUser user){
        androidDeviceId = Settings.Secure.getString(SignUpActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("demo", "storeUser: androidDeviceId "+androidDeviceId+ " "+ user.getUid());
        User u = new User();
        u.email = user.getEmail();
        u.fName = etFirstName.getText().toString().trim();
        u.lName = etLastName.getText().toString().trim();
        //u.university = dropDownValue;
        u.birthDate = etDate.getText().toString();
        u.university = universityValue;
        u.androidDeviceId = androidDeviceId;
        u.isPushEnabled = 1;
        u.id=user.getUid();
        u.lat = "35.229223";
        u.lon = "-80.839688";
        String key = user.getUid();
        Map<String, Object> userValues = u.toMap();
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(key, userValues);
        myRef.updateChildren(userUpdates);

        Intent i = new Intent(SignUpActivity.this, DashboardActivity.class);
        finish();
        startActivity(i);
    }

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
            etDate.setText(month+1+"/"+day+"/"+year);
        }
    }
}
