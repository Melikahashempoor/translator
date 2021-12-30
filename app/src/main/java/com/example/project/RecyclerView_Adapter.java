package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.View_Holder> {

    private static ArrayList<RecyclerView_Model> mdatalist = new ArrayList<>();
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onShareClick(int position, RecyclerView_Model data);
        void onCopyClick(int position, RecyclerView_Model data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class View_Holder extends RecyclerView.ViewHolder {

        public TextView source, meaning2;
        public ImageButton shareButton, copyButton;

        public View_Holder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            source = itemView.findViewById(R.id.source);
            meaning2 = itemView.findViewById(R.id.meaning2);
            shareButton = itemView.findViewById(R.id.shareIcon);
            copyButton = itemView.findViewById(R.id.copyIcon);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        RecyclerView_Model data = mdatalist.get(position);
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onShareClick(position, data);
                        }
                    }
                }
            });

            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        RecyclerView_Model data = mdatalist.get(position);
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCopyClick(position, data);
                        }
                    }
                }
            });

        }
    }

    public RecyclerView_Adapter(ArrayList<RecyclerView_Model> datalist) {
        mdatalist = datalist;
    }


    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);

        RecyclerView_Model currentItem = mdatalist.get(position);

        holder.source.setText(currentItem.getSource());
        holder.meaning2.setText(currentItem.getText());

        holder.itemView.startAnimation(animation);

    }

    @Override
    public int getItemCount() {
        return mdatalist.size();
    }

}