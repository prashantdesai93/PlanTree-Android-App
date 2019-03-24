package com.ideas2app.plantree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRefEvent, myRefQuote;
    FirebaseUser user;

    GoogleApiClient googleApiClient;
    ListView eventListView, quotesListView;
    ConcernAdapter concernAdapter;
    //QuotesAdapter quotesAdapter;
    String TAG = "demo";
    List<Concern> concernList;
    //List<Quotes> quoteList;
    String pageId = "eventPage";
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefEvent = database.getReference("allConcerns");
        //myRefQuote = database.getReference("allQuotes");

        eventListView = findViewById(R.id.lvAllEvents);
        concernList= new ArrayList<Concern>();
        Log.d(TAG, "onCreate: 3");
        initData(concernList);

//        quotesListView = findViewById(R.id.lvAllQuotes);
//        quoteList= new ArrayList<Quotes>();
//        initQuoteData(quoteList);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Concern event = null;

                event = dataSnapshot.getValue(Concern.class);
                Log.d(TAG, "Event onChildAddedddd");
                concernAdapter.add(event);


                concernAdapter.sort(new Comparator<Concern>() {
                    DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    @Override
                    public int compare(Concern o1, Concern o2) {
                        Date a1 = new Date(o1.cDate);
                        Date a2 = new Date(o2.cDate);
                        try {
                            Date d1 = f.parse(a1.toString());
                            Date d2 = f.parse(a2.toString());
                            return d1.compareTo(d2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                concernAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());
                Concern event = dataSnapshot.getValue(Concern.class);
                Concern toRemove=null;
                for(Concern t : concernList){
                    if(t.cId.equals(event.cId)){
                        toRemove=t;
                        break;
                    }
                }
                if(toRemove!=null){
                    concernAdapter.remove(toRemove);
                    concernAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Thread onCancelled", databaseError.toException());
                Toast.makeText(DashboardActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

//        ChildEventListener childEventListener1 = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Quotes quote = dataSnapshot.getValue(Quotes.class);
//                if(quote != null) {
//                    quotesAdapter.add(quote);
//                }
//                quotesAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());
//                Quotes quotes = dataSnapshot.getValue(Quotes.class);
//                Quotes toRemove=null;
//                for(Quotes t : quoteList){
//                    if(t.key.equals(quotes.key)){
//                        toRemove=t;
//                        break;
//                    }
//                }
//                if(toRemove!=null){
//                    quotesAdapter.remove(toRemove);
//                    quotesAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "Thread onChildMoved:" + dataSnapshot.getKey());
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "Thread onCancelled", databaseError.toException());
//                Toast.makeText(EventActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
//            }
//        };

        fab = findViewById(R.id.floating_action_button_fab_with_listview);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if(isConnected()) {
                        Intent i = new Intent(DashboardActivity.this, AddConcernActivity.class);
                        startActivity(i);
                } else{
                    Toast.makeText(DashboardActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("demo", "i'm here- onclick event");
                Concern event = concernList.get(position);
                Log.d(TAG, "Thread onItemClick: "+event.toString());
                if(isConnected()) {
                    Intent i = new Intent(DashboardActivity.this, ConcernDetailActivity.class);
                    i.putExtra("event", event);
                    //finish();
                    startActivity(i);
                } else{
                    Toast.makeText(DashboardActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myRefEvent.addChildEventListener(childEventListener);
    }

    public void initData(List<Concern> events){
        concernAdapter = new ConcernAdapter(DashboardActivity.this, R.layout.concern_item, events);
        eventListView.setAdapter(concernAdapter);
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

    public void signOut(){
        if(isConnected()) {
            if(googleApiClient != null){
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(DashboardActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                FirebaseAuth.getInstance().signOut();
            }

            Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
            SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, LoginActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("hasLoggedIn", false);
            editor.commit();
            finish();
            startActivity(i);
        } else{
            Toast.makeText(DashboardActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();
        super.onStart();
    }
}
