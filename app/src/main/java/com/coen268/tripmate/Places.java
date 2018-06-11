//package com.coen268.tripmate;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.support.design.widget.TabLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.coen268.tripmate.models.PlaceResponse;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.firebase.ui.auth.AuthUI.getApplicationContext;
//
//public class Places extends AppCompatActivity {
//
//    String latlng = "";
//    private List<PlaceResponse> placeResponseList;
//
//    /**
//     * The {@link android.support.v4.view.PagerAdapter} that will provide
//     * fragments for each of the sections. We use a
//     * {@link FragmentPagerAdapter} derivative, which will keep every
//     * loaded fragment in memory. If this becomes too memory intensive, it
//     * may be best to switch to a
//     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
//     */
//    private SectionsPagerAdapter mSectionsPagerAdapter;
//
//    /**
//     * The {@link ViewPager} that will host the section contents.
//     */
//    private ViewPager mViewPager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_places);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//       // mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//      //  mViewPager.setAdapter(mSectionsPagerAdapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//
//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                startActivity(new Intent(getApplicationContext(), PlaceDetails.class));
//            }
//        });
///*
//        GeocodingLocation locationAddress = new GeocodingLocation();
//        locationAddress.getAddressFromLocation("Chicago",
//                getApplicationContext(), new Places.GeocoderHandler());
//
//        fetchPlaces(latlng);
//
//    }
//
//    private void fetchPlaces(String latlng) {
//        placeResponseList = new ArrayList<>();
//        RequestQueue queue = Volley.newRequestQueue(this);
//        JSONObject o = new JSONObject();
//        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=");
//        stringBuilder.append(latlng);
//        stringBuilder.append("&radius=100000&type=museum&key=");
//        stringBuilder.append("AIzaSyAIashPBJ0qlaSCq4P0fgGy-vQkOLrtM9s");
//
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.POST,
//                stringBuilder.toString(),
//                o,
//                new Response.Listener<JSONObject>() {
//
//                    PlaceResponse placeResponse;
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("EXAMPLE", "Register Response: " + response.toString());
//                        JSONArray jsonArray = response.optJSONArray("results");
//                        for(int i=0; i<jsonArray.length(); i++){
//                            try {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                JSONObject geometry = jsonObject.getJSONObject("geometry");
//                                JSONObject location = geometry.getJSONObject("location");
//                                placeResponse = new PlaceResponse();
//                                placeResponse.setId(jsonObject.getString("place_id"));
//                                placeResponse.setName(jsonObject.getString("name"));
//                                placeResponse.setLatitude(location.getString("lat"));
//                                placeResponse.setLongitude(location.getString("lng"));
//                                placeResponseList.add(placeResponse);
//
//                                String name = jsonObject.getString("name");
//                                String lat = location.getString("lat");
//                                String lng = location.getString("lng");
//                                Log.d("Output",placeResponse.toString());
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                }) {
//
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//        };
//
//        queue.add(jsonObjectRequest);
//
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_places_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
//     * one of the sections/tabs/pages.
//     */
// /*   public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//        Bundle bundle;
//        ArrayList<String> placename;
//        ArrayList<String> lat;
//        ArrayList<String> lng;
//            switch (position) {
//                case 0:
//                /*    PlacesList tempPlace = new PlacesList();
//                    Bundle bundle = new Bundle();
//                    ArrayList<String> idList = new ArrayList<>();
//                    ArrayList<String> nameList = new ArrayList<>();
//                    for( int i = 0; i < placeResponseList.size(); i++ ) {
//                        idList.add(placeResponseList.get(i).getId());
//                        nameList.add(placeResponseList.get(i).getName());
//                    }
//                    bundle.putStringArrayList("id",idList);
//                    bundle.putStringArrayList("name", nameList);
//                    tempPlace.setArguments(bundle);
//                    return tempPlace;
//*/
//                    PlacesMap mapPlace1 = new PlacesMap();
//                    bundle = new Bundle();
//                    placename  = new ArrayList<>();
//                    lat = new ArrayList<>();
//                    lng = new ArrayList<>();
//                    for(int i = 0; i < placeResponseList.size(); i++){
//                        placename.add(placeResponseList.get(i).getName());
//                        lat.add(placeResponseList.get(i).getLatitude());
//                        lng.add(placeResponseList.get(i).getLongitude());
//                    }
//                    bundle.putStringArrayList("place name",placename);
//                    bundle.putStringArrayList("latitudes", lat);
//                    bundle.putStringArrayList("longitudes", lng);
//                    mapPlace1.setArguments(bundle);
//                    return mapPlace1;
//                case 1:
//                    PlacesMap mapPlace = new PlacesMap();
//                    bundle = new Bundle();
//                    placename  = new ArrayList<>();
//                    lat = new ArrayList<>();
//                    lng = new ArrayList<>();
//                    for(int i = 0; i < placeResponseList.size(); i++){
//                        placename.add(placeResponseList.get(i).getName());
//                        lat.add(placeResponseList.get(i).getLatitude());
//                        lng.add(placeResponseList.get(i).getLongitude());
//                    }
//                    bundle.putStringArrayList("place name",placename);
//                    bundle.putStringArrayList("latitudes", lat);
//                    bundle.putStringArrayList("longitudes", lng);
//                    mapPlace.setArguments(bundle);
//                    return mapPlace;
//                default: return null;
//            }
//        }
//
//        @Override
//        public int getCount() {
//            // Show 3 total pages.
//            return 2;
//        }
//    }
//
//    class GeocoderHandler extends Handler {
//        @Override
//        public void handleMessage(Message message) {
//            String locationAddress;
//            switch (message.what) {
//                case 1:
//                    Bundle bundle = message.getData();
//                    locationAddress = bundle.getString("address");
//                    break;
//                default:
//                    locationAddress = null;
//            }
//            latlng = locationAddress;
//        }
//    }
//}
