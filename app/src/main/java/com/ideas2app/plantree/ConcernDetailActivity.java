package com.ideas2app.plantree;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcernDetailActivity extends AppCompatActivity {

    String TAG = "demo";
    String formattedDate=null;
    Concern concern;
    TextView tvEventTitleDetail, tvEventDateDetail, tvEventTimeDetail, tvEventDescriptionDetail;
    ImageButton ibUp, ibDown;
    FirebaseDatabase database;
    DatabaseReference myRef, acceptedMembersRef;
    FirebaseUser user;
    int checkUserEventStatus = 0;
//    List<String> acceptedMembers = new ArrayList<String>();
//    List<String> acceptedMembersFName = new ArrayList<String>();
//    ExpandableListAdapter listAdapter;
//    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concern_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        if(getIntent() != null && getIntent().getExtras() != null ){
            concern = (Concern) getIntent().getSerializableExtra("event");
            Log.d(TAG, "onCreate: event "+concern.toString());
        }

        tvEventTitleDetail = findViewById(R.id.tvEventTitleDetail);
        tvEventDateDetail = findViewById(R.id.tvEventDateDetail);
        tvEventDescriptionDetail = findViewById(R.id.tvEventDescriptionDetail);

        ibUp = findViewById(R.id.ibUp);
        ibDown = findViewById(R.id.ibDown);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date(concern.cDate);
        formattedDate= formatter.format(date);

        tvEventTitleDetail.setText(concern.cTitle);
        tvEventDateDetail.setText(formattedDate);
        tvEventDescriptionDetail.setText(concern.cDescription);



        ibUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    Log.d(TAG, "onClick: in details attending " + user.getUid());
                    if (checkUserEventStatus == 0) {
                        checkUserEventStatus = 1;
                        Map<String, Object> childUpdates = new HashMap<>();

                        int up = Integer.parseInt(concern.cUpVotes) + 1;
                        childUpdates.put(user.getUid(), up);
                        myRef.child("allConcerns").child(concern.cId).child("cUpVotedUSer").updateChildren(childUpdates);
                        Toast.makeText(ConcernDetailActivity.this, "Thank You for Upvotting", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConcernDetailActivity.this, "Voted Already", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(ConcernDetailActivity.this, "Please connect to Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ibDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    Log.d(TAG, "onClick: in details attending " + user.getUid());
                    if (checkUserEventStatus == 0) {
                        checkUserEventStatus = 1;
                        Map<String, Object> childUpdates = new HashMap<>();

                        int down = Integer.parseInt(concern.cDownVotes) + 1;
                        childUpdates.put(user.getUid(), down);
                        myRef.child("allConcerns").child(concern.cId).child("cDownVotedUser").updateChildren(childUpdates);
                        Toast.makeText(ConcernDetailActivity.this, "Thank You for Downvotting", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConcernDetailActivity.this, "Voted Already", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(ConcernDetailActivity.this, "Please connect to Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Log.d(TAG, "onCreate: accepted <><><><><> "+event.toString());
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
}
