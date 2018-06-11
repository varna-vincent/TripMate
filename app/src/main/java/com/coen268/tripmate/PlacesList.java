package com.coen268.tripmate;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PlacesList extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_map_tab, container, false);

//        TextView label = (TextView) rootView.findViewById(R.id.section_label_list);
//        String searchString = getActivity().getIntent().getExtras().getString(Constants.SEARCH_STRING);
//        label.setText(searchString);
//
//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragmentListRecyclerView);
//        ListAdapter listAdapter = new ListAdapter();
//        recyclerView.setAdapter(listAdapter);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();

        if( getArguments() != null ) {
            Bundle args = getArguments();
            idList = args.getStringArrayList("id");
            nameList = args.getStringArrayList("name");
        }

        return rootView;
    }












}
