package com.coen268.tripmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coen268.tripmate.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            ((TextView) findViewById(R.id.msg)).setText(user.getDisplayName());
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login.class));
    }

    public void search(View view) {
        Intent intent = new Intent(this, Places.class);
        intent.putExtra(Constants.SEARCH_STRING, ((EditText) findViewById(R.id.txtSearch)).getText().toString());
        startActivity(intent);
    }
}
