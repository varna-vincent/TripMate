package com.coen268.tripmate;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlacesMap  extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    GoogleApiClient googleApiClient;
    ArrayList<String> placename;
    ArrayList<String> lat;
    ArrayList<String> lng;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      mView = inflater.inflate(R.layout.places_map_tab, container, false);


        placename  = new ArrayList<>();
        lat = new ArrayList<>();
        lng = new ArrayList<>();

        if( getArguments() != null ) {
            Bundle args = getArguments();
            placename = args.getStringArrayList("place name");
            lat = args.getStringArrayList("latitudes");
            lng = args.getStringArrayList("longitudes");
        }

        return mView;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.mapView);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        LatLng latLng;

        for(int i = 0; i < lat.size(); i++){
            mGoogleMap = googleMap;
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            latLng = new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(lng.get(i)));
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(placename.get(i)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
        }

    }

}

