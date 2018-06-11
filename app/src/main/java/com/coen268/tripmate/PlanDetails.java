package com.coen268.tripmate;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.coen268.tripmate.models.DestDetails;
import com.coen268.tripmate.models.TravelPlan;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlanDetails extends AppCompatActivity {

    ArrayAdapter<String> listAdapter;
    ListView myList;
    ArrayList<String> Addrs = new ArrayList<String>();
    ArrayList<String> ITEMS = new ArrayList<String>();
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
    private TextView planheader;
    private int color;
    private TravelPlan travelPlan;
    private ArrayList<String> emails = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener authStateListener;
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy 'at' h:mm a");
    private ArrayList<String> Plandets = new ArrayList<>();


    //final ArrayList<String> ITEMS = new ArrayList<String>();

   // Button deleteButton;
    //Button shareButton;

    //Boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate(R.layout.activity_plan_details, content);
        setContentView(R.layout.activity_plan_details);
        travelPlan = (TravelPlan)getIntent().getExtras().getSerializable("plan");
        planName = travelPlan.getTripName();
        color = travelPlan.getColor();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount!=null){
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        emails.add(userEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser== null){
                    Intent intent = new Intent(PlanDetails.this, Login.class);
                    startActivity(intent);
                }
            }
        };


        planNameRef = rootRef.collection("Plans").document(userEmail).collection("userPlans");
        destNameRef = planNameRef.document(planName).collection("destinations");
        ITEMS = retrievePlanDestNames(planName);
        Log.d("items", String.valueOf(ITEMS.size()));

        dates = retrievePlanDestDates(planName);
        Addrs = retrievePlanDestAddr(planName);
        planheader = findViewById(R.id.textView);
        planheader.setText(planName);
        //while( ITEMS.size() == 0 ){}
        Plandets = format();



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
        myList.setAdapter( listAdapter );
        listAdapter.notifyDataSetChanged();


        myList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                public void onItemClick( AdapterView< ? > arg0, View view, int position, long id ) {
                    Log.d("DeleteThis", myList.getItemAtPosition(position).toString());
                    if( deleteMode ) {
                       // planNameRef.document(planName).collection("destinations").document(myList.getItemAtPosition(position).toString()).delete();
                      for(int i=0;i<emails.size();i++){
                          Log.d("DELETEEMAILS", emails.get(i));
                          rootRef.collection("Plans").document(emails.get(i)).collection("userPlans").document(planName).collection("destinations").document(myList.getItemAtPosition(position).toString()).delete();
                      }
                        emails.clear();
                        listAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), sdf.format(dates.get(position)), Toast.LENGTH_LONG).show();
                    }
                }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog builder = new AlertDialog.Builder(PlanDetails.this).create();
                View dialogView = View.inflate(PlanDetails.this,R.layout.collab_adder,null);
                builder.setView(dialogView);
                final EditText email = (EditText)dialogView.findViewById(R.id.email);
                Button share = (Button)dialogView.findViewById(R.id.share);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String frndemail = email.getText().toString();
                        emails.add(frndemail);
                        Log.d("SHARE", emails.get(0));
                        rootRef.collection("Plans").document(frndemail).collection("userPlans").document(planName).set(travelPlan);
                        DestDetails destDetails;
                        for(int i=0;i<ITEMS.size();i++){
                            Log.d("ADDTOFIREBASE", ITEMS.get(i));
                            Log.d("ADDTOFIREBASE1", dates.get(i).toString());
                            destDetails = new DestDetails(ITEMS.get(i), Addrs.get(i),dates.get(i));
                            rootRef.collection("Plans").document(frndemail).collection("userPlans").document(planName).collection("destinations").document(ITEMS.get(i)).set(destDetails);
                        }

                        builder.dismiss();
                    }
                });
                  builder.show();
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
                ITEMS.clear();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    ITEMS.add(snapshot.getString("destName"));
                }
                listAdapter.notifyDataSetChanged();

            }
        });
        return ITEMS;
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
    public ArrayList<String> retrievePlanDestAddr(String planName){
        destNameRef = planNameRef.document(planName).collection("destinations");
        destNameRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Addrs.clear();
                for (DocumentSnapshot snapshot : documentSnapshots){
                    Addrs.add(snapshot.getString("destAddr"));
                }
            }
        });
        return Addrs;
    }

    public ArrayList<String> getEmails(){
        return emails;
    }
    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.signout:
                signOut();
                return true;
            case R.id.home:
                Intent intent = new Intent(PlanDetails.this,Home.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        Map<String, Object> map = new HashMap<>();
        map.put("tokenId", FieldValue.delete());
        rootRef.collection("users").document(userEmail).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseAuth.signOut();
                if (googleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(googleApiClient);
                }
            }
        });
    }

    private ArrayList<String> format(){

        ArrayList<String> Plandets = new ArrayList<>();
        for(int i=0;i<ITEMS.size();i++){
            Log.d("items", ITEMS.get(i));
            String formatedDate = sdf.format(dates.get(i));
            String namendate = ITEMS.get(i) + "-" + formatedDate;
            Plandets.add(namendate);
        }
        return Plandets;
    }
}
