package com.coen268.tripmate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coen268.tripmate.models.PlaceResponse;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;

import com.coen268.tripmate.util.Constants;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static com.coen268.tripmate.util.Constants.HOME_PLACES;
import static com.coen268.tripmate.util.Constants.PLACE_ID;

public class Home extends AppCompatActivity {

    private RecyclerView nearbyPlacesView;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private List<PlaceResponse> placeResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        fetchPlacesNearMe();

        nearbyPlacesView = (RecyclerView) findViewById(R.id.nearby_places_recyclerview);
        nearbyPlacesView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        nearbyPlacesView.setAdapter(new PlaceRecyclerAdapter());

    }

    private void fetchPlacesNearMe() {

        Task<PlaceLikelihoodBufferResponse> placeResult = null;
        placeResponseList = new ArrayList<>();

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_DENIED ) {

            placeResult = mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    Log.i(HOME_PLACES, "here");

                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    PlaceResponse placeResponse;

                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                        placeResponse = new PlaceResponse();
                        placeResponse.setName(placeLikelihood.getPlace().getName().toString());
                        placeResponse.setId(placeLikelihood.getPlace().getId());
                        placeResponseList.add(placeResponse);

                        Log.i(HOME_PLACES, String.format("Place '%s' has likelihood: %g",placeLikelihood.getPlace().getName(),placeLikelihood.getLikelihood()));
                    }

                    likelyPlaces.release();
                }
            });
        }
    }

    private void setPhotoByPlaceId(final ImageView imageView, String placeId) {

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                if(photoMetadataBuffer != null) {

                    Log.i(HOME_PLACES, String.valueOf(photoMetadataBuffer.getCount()));

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
                                Bitmap image = photo.getBitmap();
                                imageView.setImageBitmap(image);
                            }
                        });
                    }
                    photoMetadataBuffer.release();
                }
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login.class));
    }

    private class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceCardHolder> {

        @NonNull
        @Override
        public PlaceCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(Home.this).inflate(R.layout.place_card, parent, false);
            return new PlaceCardHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceCardHolder holder, int position) {

            Log.i(HOME_PLACES, placeResponseList.get(position).getName());
            holder.getPlaceCardCaption().setText(placeResponseList.get(position).getName());
            setPhotoByPlaceId(holder.getPlaceCardPhoto(), placeResponseList.get(position).getId());
            setPlaceItemClickListener(holder.getParentLayout(), placeResponseList.get(position).getId());
        }

        @Override
        public int getItemCount() {
            return placeResponseList.size();
        }
    }

    private void setPlaceItemClickListener(LinearLayout parentLayout, final String placeId) {
        final Activity self = this;
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(self, PlaceDetails.class);
                intent.putExtra(PLACE_ID, placeId);
                startActivity(intent);
            }
        });
    }

    private class PlaceCardHolder extends RecyclerView.ViewHolder {

        LinearLayout parentLayout;
        ImageView placeCardPhoto;
        TextView placeCardCaption;

        public PlaceCardHolder(View itemView) {

            super(itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            placeCardPhoto = (ImageView) itemView.findViewById(R.id.placeCardPhoto);
            placeCardCaption = (TextView) itemView.findViewById(R.id.placeCardCaption);
        }

        public LinearLayout getParentLayout() {
            return parentLayout;
        }

        public void setParentLayout(LinearLayout parentLayout) {
            this.parentLayout = parentLayout;
        }

        public ImageView getPlaceCardPhoto() {
            return placeCardPhoto;
        }

        public void setPlaceCardPhoto(ImageView placeCardPhoto) {
            this.placeCardPhoto = placeCardPhoto;
        }

        public TextView getPlaceCardCaption() {
            return placeCardCaption;
        }

        public void setPlaceCardCaption(TextView placeCardCaption) {
            this.placeCardCaption = placeCardCaption;
        }
    }

}
