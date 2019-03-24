package com.ideas2app.plantree;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    Button btnSignUp;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String email, password;
    EditText etUserName, etPassword;
    public static String SHARED_PREFS_NAME = "SharedPrefs";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();
        etUserName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpSelectActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                try{
                    if(etUserName.getText().length()>1){
                        email = etUserName.getText().toString().trim();
                    }else{
                        error = true;
                        etUserName.setError("Required");
                    }
                    if(etPassword.getText().length()>1){
                        password = etPassword.getText().toString();
                    }else{
                        error = true;
                        etPassword.setError("Required");
                    }
                }catch(Exception e){

                }

                if(!error){
                    if(isConnected()) {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.getWindow().setBackgroundDrawable(new
                                ColorDrawable(android.graphics.Color.TRANSPARENT));
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.progressdialog);
                        performLogin(email, password);
                    } else{
                        Toast.makeText(LoginActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
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

    public void printToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void performLogin(String email, String password) {
        Log.d("demo", "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Logged in Successfully",
                                    Toast.LENGTH_SHORT).show();
                            //getMessageThreads(mAuth.getCurrentUser());
                            SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS_NAME, LoginActivity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean("hasLoggedIn", true);
                            editor.commit();
                            Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                            finish();
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "signInWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
