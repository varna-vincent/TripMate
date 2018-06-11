package com.coen268.tripmate;


import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coen268.tripmate.models.PlaceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceFragment extends AppCompatActivity {

    Button firstFragment, secondFragment;
    String input;
    private List<PlaceResponse> placeResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_fragment);


        firstFragment = (Button) findViewById(R.id.firstFragment);
        secondFragment = (Button) findViewById(R.id.secondFragment);

        input = "Sydney";
        fetchPlaces(input);


        firstFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PlacesList(), 0);
            }
        });

        secondFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PlacesMap(), 1);
            }
        });
    }



    private void fetchPlaces(String input) {
        placeResponseList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject o = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?query=museums+in+");
        stringBuilder.append(input);
        stringBuilder.append("&key=AIzaSyAIashPBJ0qlaSCq4P0fgGy-vQkOLrtM9s");
        Log.d("REQUEST", stringBuilder.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                 stringBuilder.toString(),
                o,
                new Response.Listener<JSONObject>() {

                    PlaceResponse placeResponse;

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("EXAMPLE", "Register Response: " + response.toString());
                        JSONArray jsonArray = response.optJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject geometry = jsonObject.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                placeResponse = new PlaceResponse();
                                placeResponse.setId(jsonObject.getString("place_id"));
                                placeResponse.setName(jsonObject.getString("name"));
                                placeResponse.setLatitude(location.getString("lat"));
                                placeResponse.setLongitude(location.getString("lng"));
                                placeResponseList.add(placeResponse);

                                String name = jsonObject.getString("name");
                                String lat = location.getString("lat");
                                String lng = location.getString("lng");
                                Log.d("Output", name + lat + lng);

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

    private void loadFragment(Fragment fragment, int flag) {
        Bundle bundle;
        ArrayList<String> idList;
        ArrayList<String> placename;
        ArrayList<String> lat;
        ArrayList<String> lng;


        if (flag == 0) {
            bundle = new Bundle();
            placename = new ArrayList<String>();
            idList = new ArrayList<String>();
            for (int i = 0; i < placeResponseList.size(); i++) {
                idList.add(placeResponseList.get(i).getId());
                placename.add(placeResponseList.get(i).getName());
            }
            bundle.putStringArrayList("id", idList);
            bundle.putStringArrayList("name", placename);
        } else {
            bundle = new Bundle();
            placename = new ArrayList<>();
            lat = new ArrayList<>();
            lng = new ArrayList<>();
            for (int i = 0; i < placeResponseList.size(); i++) {
                placename.add(placeResponseList.get(i).getName());
                lat.add(placeResponseList.get(i).getLatitude());
                lng.add(placeResponseList.get(i).getLongitude());
            }
            bundle.putStringArrayList("place name", placename);
            bundle.putStringArrayList("latitudes", lat);
            bundle.putStringArrayList("longitudes", lng);
        }

        // FragmentManager fm = getFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }

/*
    protected class GeocoderHandler extends Handler {

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    latlng = bundle.getString("address");
                    break;
                default:
                    latlng = "KU6 BHI";
            }
        }
    }
    */
}