package com.coen268.tripmate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ListAdapter extends RecyclerView.Adapter{

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plce_list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mItemText;
        private ImageView mItemImage;

        public ListViewHolder(View itemView){
                super(itemView);
                mItemText = (TextView)  itemView.findViewById(R.id.list_item_name);
                mItemImage = (ImageView) itemView.findViewById(R.id.list_item_image);

                itemView.setOnClickListener(this);
        }

        public void bindView(int position){
              //  mItemText.setText();
            //mItemImage.setImageResource();
        }

        public void onClick(View view){

        }


    }



}
