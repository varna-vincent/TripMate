package com.coen268.tripmate;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class PlanDetails extends AppCompatActivity {

    ArrayAdapter<String> listAdapter;
    ListView myList;
    ArrayList<String> ITEMS = new ArrayList<String>();
    ArrayList<String> destsList = new ArrayList<String>();
    ArrayList<Date> dates = new ArrayList<Date>();
    private CollectionReference planNameRef;
    private CollectionReference destNameRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore rootRef;
    Button deleteButton;
    Button shareButton;
    String planName;
    Boolean deleteMode = false;
    PlaceDetails pp = new PlaceDetails();
    private String userEmail;
    private String userName;
  
    final ArrayList<String> ITEMS = new ArrayList<String>();

    Button deleteButton;
    Button shareButton;

    Boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        planName = getIntent().getExtras().getString("plan");

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount!=null){
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();

        planNameRef = rootRef.collection("Plans").document(userEmail).collection("userPlans");
        destNameRef = planNameRef.document(planName).collection("destinations");
        ITEMS = retrievePlanDestNames(planName);

        /*// ITEMS = getIntent().getExtras().getStringArrayList( "data" );
        for( int i = 1; i < 11; i++ ) {
            ITEMS.add( "Place " + String.valueOf(i) );
        }*/

        listAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, ITEMS);

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setBackgroundColor(Color.WHITE);
        shareButton = findViewById(R.id.shareButton);
        shareButton.setBackgroundColor(Color.WHITE);

        myList = ( ListView ) findViewById(R.id.myListView);
        myList.setClickable( false );
        listAdapter.notifyDataSetChanged();
        myList.setAdapter( listAdapter );

        myList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                public void onItemClick( AdapterView< ? > arg0, View view, int position, long id ) {
                    if( deleteMode ) {
                        ITEMS.remove(position);
                        listAdapter.notifyDataSetChanged();
                    }
                }
        });
    }

    void clickDeleteButton( View v ) {
        deleteMode = !deleteMode;
        if( deleteMode ) {
            deleteButton.setBackgroundColor(Color.RED);
            myList.setClickable( true );
        } else {
            deleteButton.setBackgroundColor(Color.WHITE);
            myList.setClickable( false );
        }
    }

    void clickShareButton( View v ) {
        Intent intent = new Intent( v.getContext(), NavigationDrawer.class);
        startActivity( intent );
    }

    public ArrayList<String> retrievePlanDestNames(String planName){
        destNameRef = planNameRef.document(planName).collection("destinations");
        destNameRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                destsList.clear();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    destsList.add(snapshot.getString("destName"));
                }
                listAdapter.notifyDataSetChanged();

            }
        });
        return destsList;
    }

    public ArrayList<Date> retrievePlanDestDates(String planName){
        destNameRef = planNameRef.document(planName).collection("destinations");
        destNameRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                dates.clear();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    dates.add(snapshot.getDate("date"));
                }
            }
        });
        return dates;
    }
}
