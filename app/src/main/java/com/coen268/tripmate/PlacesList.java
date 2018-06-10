package com.coen268.tripmate;

import android.support.v4.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.coen268.tripmate.util.Constants;

public class PlacesList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_list_tab, container, false);

        //TextView label = (TextView) rootView.findViewById(R.id.section_label_list);
        //String searchString = getActivity().getIntent().getExtras().getString(Constants.SEARCH_STRING);
        //label.setText(searchString);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragmentListRecyclerView);
        ListAdapter listAdapter = new ListAdapter();
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }




}
