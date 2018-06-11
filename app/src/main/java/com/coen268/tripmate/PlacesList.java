package com.coen268.tripmate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coen268.tripmate.util.Constants;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.coen268.tripmate.util.Constants.HOME_PLACES;
import static com.coen268.tripmate.util.Constants.PLACE_ID;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PlacesList extends Fragment {

    private RecyclerView textSearchPlacesView;
    private PlacesList.PlaceRecyclerAdapter mAdapter;

    private GeoDataClient mGeoDataClient;
    List<String> idList;
    List<String> nameList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_list_tab, container, false);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        if( getArguments() != null ) {
            Bundle args = getArguments();
            idList = args.getStringArrayList("id");
            nameList = args.getStringArrayList("name");
            Log.i("names of places", nameList.get(0));
            Log.i("id of places", idList.get(0));
        }

        textSearchPlacesView = (RecyclerView) rootView.findViewById(R.id.textsearch_places_recyclerview);
        textSearchPlacesView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new PlacesList.PlaceRecyclerAdapter();
        textSearchPlacesView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return rootView;
    }

    private class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlacesList.PlaceCardHolder> {

        @NonNull
        @Override
        public PlacesList.PlaceCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.place_card, parent, false);
            return new PlacesList.PlaceCardHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlacesList.PlaceCardHolder holder, int position) {

            Log.i(HOME_PLACES, nameList.get(position));
            holder.getPlaceCardCaption().setText(nameList.get(position));
            setPhotoByPlaceId(holder.getPlaceCardPhoto(), idList.get(position));
            setPlaceItemClickListener(holder.getParentLayout(), idList.get(position));
        }

        @Override
        public int getItemCount() {
            return nameList.size();
        }
    }

    private void setPlaceItemClickListener(LinearLayout parentLayout, final String placeId) {

        final Activity self = getActivity();
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
}
