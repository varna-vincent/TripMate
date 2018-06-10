package com.coen268.tripmate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coen268.tripmate.util.Constants;
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

public class PlacesMap  extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    GoogleApiClient googleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.places_map_tab, container, false);

       // TextView label = (TextView) rootView.findViewById(R.id.section_label_map);
       // String searchString = getActivity().getIntent().getExtras().getString(Constants.SEARCH_STRING);
       // label.setText(searchString);

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
       // mGoogleMap = googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.addMarker(new MarkerOptions().position())

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JSONObject o = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=50000&type=museum&key=AIzaSyB10cSEJ8OT8PQ4yX3ZcQSmPcMoEHvv7kg",
                o,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("EXAMPLE", "Register Response: " + response.toString());
                        JSONArray jsonArray = response.optJSONArray("results");
                        for(int i=0; i<jsonArray.length(); i++){
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                JSONObject geometry = jsonObject.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                String lat = location.getString("lat");
                                String lng = location.getString("lng");

                                mGoogleMap = googleMap;
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                                Log.d("Output",name + lat + lng);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        queue.add(jsonObjectRequest);

    }
}

