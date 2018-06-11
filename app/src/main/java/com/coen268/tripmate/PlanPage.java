package com.coen268.tripmate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coen268.tripmate.models.PlaceResponse;
import com.coen268.tripmate.models.TravelPlan;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.coen268.tripmate.util.Constants.HOME_PLACES;
import static com.coen268.tripmate.util.Constants.PLACE_ID;

public class PlanPage extends AppCompatActivity {

    private static final String LIST_STATE_KEY = "list-state";
    private RecyclerView myPlansView;
    private Bundle mBundleRecyclerViewState;
    private PlaceRecyclerAdapter mAdapter;

    private List<TravelPlan> plans;
    private CollectionReference planNameRef;

    public String userEmail;
    public String userName;

    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_page);
        myPlansView = (RecyclerView) findViewById(R.id.my_plans_recyclerview);
        myPlansView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new PlaceRecyclerAdapter();
        myPlansView.setAdapter(mAdapter);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount!=null){
            userEmail = googleSignInAccount.getEmail();
            userName = googleSignInAccount.getDisplayName();
        }
        planNameRef = rootRef.collection("Plans").document(userEmail).collection("userPlans");

        retrieveUserPlans();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = myPlansView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(LIST_STATE_KEY, listState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(LIST_STATE_KEY);
            myPlansView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login.class));
    }

    private class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceCardHolder> {

        @NonNull
        @Override
        public PlaceCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(PlanPage.this).inflate(R.layout.plan_card, parent, false);
            return new PlaceCardHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceCardHolder holder, int position) {
            Log.i(HOME_PLACES, plans.get(position).getTripName());
            holder.getPlaceCardCaption().setText(plans.get(position).getTripName());
            holder.getParentLayout().setBackgroundColor(plans.get(position).getColor());
            setPlaceItemClickListener(holder.getParentLayout(), plans.get(position).getTripName());
        }

        @Override
        public int getItemCount() {
            return plans.size();
        }
    }

    private void setPlaceItemClickListener(LinearLayout parentLayout, final String planName) {
        final Activity self = this;
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(self, PlanDetails.class);
                intent.putExtra("plan", planName);
                startActivity(intent);
            }
        });
    }

    private class PlaceCardHolder extends RecyclerView.ViewHolder {

        LinearLayout parentLayout;
        TextView placeCardCaption;

        public PlaceCardHolder(View itemView) {

            super(itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentPlanLayout);
            placeCardCaption = (TextView) itemView.findViewById(R.id.planCardCaption);
        }

        public LinearLayout getParentLayout() {
            return parentLayout;
        }

        public void setParentLayout(LinearLayout parentLayout) {
            this.parentLayout = parentLayout;
        }

        public TextView getPlaceCardCaption() {
            return placeCardCaption;
        }

        public void setPlaceCardCaption(TextView placeCardCaption) {
            this.placeCardCaption = placeCardCaption;
        }
    }

    public void retrieveUserPlans(){
        plans = new ArrayList<>();
        planNameRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                TravelPlan travelPlan;
                for (DocumentSnapshot snapshot : documentSnapshots){
                    travelPlan = new TravelPlan();
                    travelPlan.setTripId(snapshot.getString("tripId"));
                    travelPlan.setTripName(snapshot.getString("tripName"));
                    travelPlan.setColor(Integer.parseInt(snapshot.getString("color").trim()));
                    plans.add(travelPlan);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}

