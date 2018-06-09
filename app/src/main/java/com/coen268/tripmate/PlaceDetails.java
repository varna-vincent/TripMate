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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Date;

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
    private TextView textView;
    private TextView textView1;

    protected GeoDataClient mGeoDataClient;
    CharSequence toastMsg;
    Place myPlace;
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceDetails.this);
                builder.setTitle("Create a New Plan");

                TableLayout tableLayout = new TableLayout(PlaceDetails.this);
                TableRow tableRow = new TableRow(PlaceDetails.this);
                textView = new TextView(PlaceDetails.this);
                textView1 = new TextView(PlaceDetails.this);
                textView.setText("Add to existing plan");
                textView1.setText("Create new plan");
                button = new Button(PlaceDetails.this);
                button1 = new Button(PlaceDetails.this);
                button.setText("Add");
                button1.setText("Create and Add");
                spinner = new Spinner(PlaceDetails.this);
                editText = new EditText(PlaceDetails.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                //  ll.addView(editText);
                tableRow.addView(textView);
                tableRow.addView(spinner);
                tableRow.addView(button);
                TableRow tableRow1 = new TableRow(PlaceDetails.this);
                tableRow1.addView(textView1);
                tableRow1.addView(editText);
                tableRow1.addView(button1);
                tableLayout.addView(tableRow);
                tableLayout.addView(tableRow1);
                builder.setView(tableLayout);
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
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String planName = spinner.getSelectedItem().toString();
                        destNameRef = planNameRef.document(planName).collection("destinations");
                        String destId = destNameRef.document().getId();
                        Date date = new Date();
                        DestDetails destDetails = new DestDetails("destname",date);
                        destNameRef.document(destId).set(destDetails);
                    }
                });
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String planName = editText.getText().toString();
                        addPlan(planName);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        String placeId = "ChIJhYiFmCAKlVQRjC7EI-INETU";
        fetchPlaceDetails(placeId);
        getPhotos(placeId);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addToPlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlaceDetails.class);
                intent.putExtra(PLACE_NAME, myPlace.getName());
                startActivity(new Intent(getApplicationContext(), PlaceDetails.class));
            }
        });*/
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

    private void addPlan(final String planName){
        String planId = planNameRef.document().getId();
        TravelPlan travelPlan = new TravelPlan(planName,planId);
        planNameRef.document(planId).set(travelPlan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                destNameRef = planNameRef.document(planName).collection("destinations");
                String destId = destNameRef.document().getId();
                Date date = new Date();
                DestDetails destDetails = new DestDetails("destname",date);
                destNameRef.document(destId).set(destDetails);
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


}
