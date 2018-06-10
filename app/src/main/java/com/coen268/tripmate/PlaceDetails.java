package com.coen268.tripmate;

import android.content.DialogInterface;
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
import android.widget.RatingBar;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.coen268.tripmate.util.Constants.PLACE_ID;
import static com.coen268.tripmate.util.Constants.PLACE_NAME;

public class PlaceDetails extends AppCompatActivity {
    private String userName;
    private String userEmail;
    private EditText editText;
    private CollectionReference planNameRef;
    private CollectionReference destNameRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore rootRef;
    private Spinner spinner;
    private ArrayList<String> plansList = new ArrayList<>();
    private Button button;
    private Button button1;
    private Button button2;

    private String destinationName;
    private Button redButton;
    private Button blueButton;
    private Button greenButton;
    private Button yellowButton;

    private TextView textView;
    private TextView textView1;
    private Button setDnT;

    private TravelPlan travelPlan;

    protected GeoDataClient mGeoDataClient;
    CharSequence toastMsg;
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

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // "ChIJhYiFmCAKlVQRjC7EI-INETU"
        String placeId = getIntent().getStringExtra(PLACE_ID);
        fetchPlaceDetails(placeId);
        getPhotos(placeId);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();

        planNameRef = rootRef.collection("Plans").document(userEmail).collection("userPlans");
        FloatingActionButton fab = findViewById(R.id.addToPlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog builder = new AlertDialog.Builder(PlaceDetails.this).create();
                final View addPlan = View.inflate(PlaceDetails.this, R.layout.addplans_dialog, null);
                button = addPlan.findViewById(R.id.add);
                button1 = addPlan.findViewById(R.id.create);
                button2 = addPlan.findViewById(R.id.cancel);

                spinner = addPlan.findViewById(R.id.spinner);
                editText = addPlan.findViewById(R.id.edittext);
                editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                redButton = addPlan.findViewById(R.id.btn_pale_red);
                blueButton = addPlan.findViewById(R.id.btn_pale_blue);
                greenButton = addPlan.findViewById(R.id.btn_pale_green);
                yellowButton = addPlan.findViewById(R.id.btn_pale_yellow);

                travelPlan = new TravelPlan();

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

                redButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        travelPlan.setColor(getResources().getString(0 + R.color.pale_red));
                    }
                });

                blueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        travelPlan.setColor(getResources().getString(0 + R.color.pale_blue));
                    }
                });

                greenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        travelPlan.setColor(getResources().getString(0 + R.color.pale_green));
                    }
                });

                yellowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        travelPlan.setColor(getResources().getString(0 + R.color.pale_yellow));
                    }
                });

                builder.setView(addPlan);
                builder.show();
            }
        });
    }

    private void fetchPlaceDetails(String placeId) {

        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();

                    Place myPlace = places.get(0);
                    updatePlaceDetailsUI(myPlace);
                    destinationName = myPlace.getName().toString();
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
        travelPlan.setTripName(planName);
        travelPlan.setTripId(planId);
//        TravelPlan travelPlan = new TravelPlan(planName,planId);
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
                if(photoMetadataBuffer != null && photoMetadataBuffer.getCount() > 0) {

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
                photoMetadataBuffer.release();
            }
        });
    }

    private void updatePlacePhotoUI(Bitmap photoBitmap) {
        ImageView placeImage = (ImageView) findViewById(R.id.placePhoto);
        placeImage.setImageBitmap(photoBitmap);
    }

    private void updatePlaceDetailsUI(Place myPlace) {

        ((TextView) findViewById(R.id.placeName)).setText(myPlace.getName());
        ((TextView) findViewById(R.id.txtAddress)).setText(myPlace.getAddress());
        ((TextView) findViewById(R.id.txtPlacePhone)).setText(myPlace.getPhoneNumber());
        ((TextView) findViewById(R.id.txtPriceLevel)).setText(parsePriceLevel(myPlace.getPriceLevel()));
        ((RatingBar) findViewById(R.id.txtPlaceRating)).setRating(myPlace.getRating());
        if(myPlace.getWebsiteUri() != null) {
            ((TextView) findViewById(R.id.txtWebAddress)).setText(myPlace.getWebsiteUri().toString());
        }
    }

    private String parsePriceLevel(int priceLevel) {
        switch (priceLevel) {
            case 0: return "($)";
            case 1: return "($$)";
            case 2: return "($$$)";
            case 3: return "($$$$)";
            case 4: return "($$$$$)";
            default: return "";
        }
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
                        DestDetails destDetails = new DestDetails(destinationName, day);
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