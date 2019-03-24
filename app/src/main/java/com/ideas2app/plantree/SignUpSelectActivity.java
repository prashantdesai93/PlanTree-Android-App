package com.ideas2app.plantree;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignUpSelectActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;
    public static int RC_SIGN_IN = 9001;
    Button tvManualSetUp;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog progressDialog;
    String androidDeviceId, universityValue;
    String token="";
    //CharSequence[] universityArray = {"UNC Charlotte", "UNC Chapel-Hill", "University of Texas Dallas", "Northeastern University", "Carnegie Mellon University"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_select);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        token = FirebaseInstanceId.getInstance().getToken();

        if(isConnected()) {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
        } else{
            Toast.makeText(SignUpSelectActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickQuickSignUp(View v) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSelectActivity.this);
//        builder.setTitle("Select University")
//                .setCancelable(false)
//                .setSingleChoiceItems(universityArray, -1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d("demo", (String) universityArray[which]);
//                        universityValue = universityArray[which].toString();
//                    }
//                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d("demo","clicked OK");
//                        if(universityValue!=null && !universityValue.equals("")){
                            progressDialog = new ProgressDialog(SignUpSelectActivity.this);
                            progressDialog.getWindow().setBackgroundDrawable(new
                                    ColorDrawable(android.graphics.Color.TRANSPARENT));
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.progressdialog);
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                            startActivityForResult(signInIntent, RC_SIGN_IN);
//                        } else{
//                            Toast.makeText(SignUpSelectActivity.this, "Please select University", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
        //AlertDialog ad = builder.create();
        //ad.show();

    }

    public void onClickManualSetUp(View v) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSelectActivity.this);
//        builder.setTitle("Select University")
//                .setCancelable(false)
//                .setSingleChoiceItems(universityArray, -1, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d("demo", (String) universityArray[which]);
//                        universityValue = universityArray[which].toString();
//                    }
//                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d("demo","clicked OK");
//                        if(universityValue!=null && !universityValue.equals("")){
                            Intent i = new Intent(SignUpSelectActivity.this, SignUpActivity.class);
                            i.putExtra("universityValue", universityValue);
                            startActivity(i);
//                        } else{
//                            Toast.makeText(SignUpSelectActivity.this, "Please select University", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("demo","clicked Cancel");
//            }
//        });
//        AlertDialog ad = builder.create();
//        ad.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("demo", String.valueOf(resultCode == RESULT_OK));
        if(requestCode == RC_SIGN_IN){
            //Log.d("demo", "inside result<><><>: ");

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Log.d("demo", "after googleSignInResult<><><>: ");

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("demo", "Google sign in failed", e);
                // ...
            }

            handleSignInResult(googleSignInResult);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("demo", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    public void handleSignInResult(GoogleSignInResult googleSignInResult){
        Log.d("demo", "inside handleSignInResult<><><>: "+googleSignInResult.isSuccess());
        if(googleSignInResult.isSuccess()){
            //Log.d("demo", "inside handleSignInResult success<><><>: ");
            GoogleSignInAccount acc = googleSignInResult.getSignInAccount();
            progressDialog.dismiss();
            Log.d("demo", "handleSignInResult<><><>: "+acc.getDisplayName());
            storeUser(acc);
        } else{
            progressDialog.dismiss();
            Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*public  void updateUserProfile(GoogleSignInAccount acc){
        final FirebaseUser user = acc;
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(acc.getDisplayName())
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
    }*/

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

    public void storeUser(GoogleSignInAccount user){
        androidDeviceId = Settings.Secure.getString(SignUpSelectActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("demo", "storeUser: androidDeviceId "+androidDeviceId);
        Log.d("demo", "storeUser: user "+user.getDisplayName().split(" ")[0]);
        Log.d("demo", "storeUser: user "+user.getDisplayName().split(" ")[1]);
        User u = new User();
        u.email = user.getEmail();
        u.fName = user.getDisplayName().split(" ")[0];
        u.lName = user.getDisplayName().split(" ")[1];
        //u.birthDate = etDate.getText().toString();
        u.birthDate = "Need to check";
        u.university = universityValue;
        u.androidDeviceId = token;
        u.isPushEnabled = 1;
        u.id=user.getId();
        String key = user.getId();
        Map<String, Object> userValues = u.toMap();
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(key, userValues);
        myRef.updateChildren(userUpdates);

        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, LoginActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();

        Intent i = new Intent(SignUpSelectActivity.this, DashboardActivity.class);
        finish();
        startActivity(i);
    }
}
