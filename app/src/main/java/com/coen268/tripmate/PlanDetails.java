package com.coen268.tripmate;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class PlanDetails extends AppCompatActivity {

    ArrayAdapter<String> listAdapter;
    ListView myList;
    final ArrayList<String> ITEMS = new ArrayList<String>();

    Button deleteButton;
    Button shareButton;

    Boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        //ArrayList<String> ITEMS = new ArrayList<String>();
        // ITEMS = getIntent().getExtras().getStringArrayList( "data" );
        for( int i = 1; i < 11; i++ ) {
            ITEMS.add( "Place " + String.valueOf(i) );
        }

        listAdapter = new ArrayAdapter<String>( this,
                android.R.layout.simple_list_item_1,
                ITEMS
        );

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setBackgroundColor(Color.WHITE);
        shareButton = findViewById(R.id.shareButton);
        shareButton.setBackgroundColor(Color.WHITE);

        myList = ( ListView ) findViewById(R.id.myListView);
        myList.setClickable( false );
        myList.setAdapter( listAdapter );

        myList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                public void onItemClick( AdapterView< ? > arg0, View view, int position, long id ) {
                    if( deleteMode ) {
                        ITEMS.remove(position);
                        listAdapter.notifyDataSetChanged();
                    }
                }
        });
    }

    void clickDeleteButton( View v ) {
        deleteMode = !deleteMode;
        if( deleteMode ) {
            deleteButton.setBackgroundColor(Color.RED);
            myList.setClickable( true );
        } else {
            deleteButton.setBackgroundColor(Color.WHITE);
            myList.setClickable( false );
        }
    }

    void clickShareButton( View v ) {
        Intent intent = new Intent( v.getContext(), NavigationDrawer.class);
        startActivity( intent );
    }
}
