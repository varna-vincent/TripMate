package com.coen268.tripmate;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.coen268.tripmate.models.DestDetails;
import com.coen268.tripmate.models.TravelPlan;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.coen268.tripmate.util.Constants.PLACE_NAME;

public class PlaceDetails extends AppCompatActivity {
    private String userName;
    private String userEmail;
    private EditText editText;
    private CollectionReference planNameRef;
    private CollectionReference destNameRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore rootRef;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Spinner spinner;
    private ArrayList<String> plansList = new ArrayList<>();
   private Button button;
    private Button button1;
    private Button button2;

    private TextView textView;
    private TextView textView1;
    private Button setDnT;

    protected GeoDataClient mGeoDataClient;
    CharSequence toastMsg;
    Place myPlace;
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        //Retrieve User  Details
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount!=null){
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();

        planNameRef = rootRef.collection("Plans").document(userEmail).collection("userPlans");
        FloatingActionButton fab = findViewById(R.id.addToPlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog builder = new AlertDialog.Builder(PlaceDetails.this).create();
                final View addPlan = View.inflate(PlaceDetails.this, R.layout.addplans_dialog, null);
                button = addPlan.findViewById(R.id.add);
                button1 = addPlan.findViewById(R.id.create);
                button2 = addPlan.findViewById(R.id.cancel);

                spinner = addPlan.findViewById(R.id.spinner);
                editText = addPlan.findViewById(R.id.edittext);
                editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                //Retrieve plans created by user and populate spinner
                planNameRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        plansList.clear();
                        for (DocumentSnapshot snapshot : documentSnapshots){
                            plansList.add(snapshot.getString("tripName"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_selectable_list_item,plansList);
                        adapter.notifyDataSetChanged();
                        spinner.setAdapter(adapter);
                    }
                });
                //On Add to Plan button click
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String planName = spinner.getSelectedItem().toString();
                        destNameRef = planNameRef.document(planName).collection("destinations");
                        builder.dismiss();
                        setDateandTime();
                    }
                });
                //On Create Plan and Add button click
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String planName = editText.getText().toString();
                        addPlan(planName);
                        builder.dismiss();
                        setDateandTime();

                    }
                });
                //On Cancel button click
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.dismiss();
                    }
                });

                builder.setView(addPlan);
                builder.show();
            }
        });

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        String placeId = "ChIJx2utCDvsloARNesiBmb2frc";
        fetchPlaceDetails(placeId);
        getPhotos(placeId);

    }

    private void fetchPlaceDetails(String placeId) {

        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    myPlace = places.get(0);
                    updatePlaceDetailsUI(myPlace);
                    Log.i(PLACE_NAME, "Place found: " + myPlace.getName());
                    places.release();
                } else {
                    Log.e(PLACE_NAME, task.getException().toString(), task.getException());
                    Log.e(PLACE_NAME, "Place not found.");
                    toastMsg = "Place not found.";
                    Toast toast = Toast.makeText(getApplicationContext(), toastMsg, duration);
                    toast.show();
                }
            }
        });
    }

    //Function to create a new plan for the user
    private void addPlan(final String planName){
        String planId = planNameRef.document().getId();
        TravelPlan travelPlan = new TravelPlan(planName,planId);
        planNameRef.document(planName).set(travelPlan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                destNameRef = planNameRef.document(planName).collection("destinations");
                Log.d("TAG", "Success!");
            }
        });
    }


    // Request photos and metadata for the specified place.
    private void getPhotos(String placeId) {

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        updatePlacePhotoUI(bitmap);
                    }
                });
            }
        });
    }

    private void updatePlacePhotoUI(Bitmap photoBitmap) {
        ImageView placeImage = (ImageView) findViewById(R.id.placePhoto);
        placeImage.setImageBitmap(photoBitmap);
    }

    private void updatePlaceDetailsUI(Place myPlace) {

        ((TextView) findViewById(R.id.placeName)).setText(myPlace.getName());
    }


    //Select Date and Time and save destination details
    private void setDateandTime(){
        final AlertDialog builder1 = new AlertDialog.Builder(PlaceDetails.this).create();
        final View dialogView = View.inflate(PlaceDetails.this, R.layout.date_layout, null);
        setDnT = dialogView.findViewById(R.id.date_set);
        setDnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePicker editDate = (DatePicker) dialogView.findViewById(R.id.date_picker);
                builder1.dismiss();
                final AlertDialog builder2 = new AlertDialog.Builder(PlaceDetails.this).create();
                final View dialogView1 = View.inflate(PlaceDetails.this, R.layout.time_layout, null);
                Button setTime = dialogView1.findViewById(R.id.time_set);
                setTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TimePicker editTime = (TimePicker) dialogView1.findViewById(R.id.time_picker);
                        Calendar calendar = new GregorianCalendar(editDate.getYear(),
                                editDate.getMonth(),
                                editDate.getDayOfMonth(),
                                editTime.getCurrentHour(),
                                editTime.getCurrentMinute());
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy 'at' h:mm a");
                        Date day = calendar.getTime();
                        String formatedDate = sdf.format(day);
                        DestDetails destDetails = new DestDetails("destname",day);
                        String destId = destNameRef.document().getId();
                        destNameRef.document(destId).set(destDetails);
                        builder2.dismiss();
                    }
                });
                builder2.setView(dialogView1);
                builder2.show();

            }
        });
        builder1.setView(dialogView);
        builder1.show();
    }
}
