package com.coen268.tripmate;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coen268.tripmate.models.PlaceResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceFragment extends AppCompatActivity {

    Button firstFragment, secondFragment;
    String input;
    private List<PlaceResponse> placeResponseList;
    private String userName;
    private String userEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore rootRef;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_fragment);


        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount!=null){
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseFirestore.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser== null){
                    Intent intent = new Intent(PlaceFragment.this, Login.class);
                    startActivity(intent);
                }
            }
        };
        firstFragment = (Button) findViewById(R.id.firstFragment);
        secondFragment = (Button) findViewById(R.id.secondFragment);

        Intent intent = getIntent();
        input = intent.getStringExtra("SearchString");

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
    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.signout:
                signOut();
                return true;
            case R.id.planner:
                Intent intent = new Intent(PlaceFragment.this,PlanPage.class);
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