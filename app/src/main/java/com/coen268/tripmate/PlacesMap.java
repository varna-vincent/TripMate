package com.coen268.tripmate;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coen268.tripmate.util.Constants;

public class PlacesMap  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_map_tab, container, false);

        TextView label = (TextView) rootView.findViewById(R.id.section_label_map);
        String searchString = getActivity().getIntent().getExtras().getString(Constants.SEARCH_STRING);
        label.setText(searchString);

        return rootView;
    }
}
