package com.coen268.tripmate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PlaceDetails extends AppCompatActivity {

    protected GeoDataClient mGeoDataClient;
    private static final String TAG = "Place Details";
    CharSequence toastMsg;
    int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        String placeId = "ChIJx2utCDvsloARNesiBmb2frc";
        fetchPlaceDetails(placeId);
        getPhotos(placeId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addToPlan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PlaceDetails.class));
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
                    Log.i(TAG, "Place found: " + myPlace.getName());
                    places.release();
                } else {
                    Log.e(TAG, task.getException().toString(), task.getException());
                    Log.e(TAG, "Place not found.");
                    toastMsg = "Place not found.";
                    Toast toast = Toast.makeText(getApplicationContext(), toastMsg, duration);
                    toast.show();
                }
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
}
