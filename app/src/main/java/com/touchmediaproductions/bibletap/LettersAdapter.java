package com.touchmediaproductions.bibletap;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class LettersAdapter extends RecyclerView.Adapter {

    protected final Context context;
    private LinkedList<Character> mDataset;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(final View parent) {
            super(parent);
            textView = (TextView) itemView.findViewById(R.id.textview_main_letters_large);;
        }

        public void bindData(final Character character) {
            textView.setText(character.toString());
            if(getAdapterPosition() == 0){
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorTextAccent));
                textView.setTextSize(50f);
            }
        }
    }


    public LettersAdapter(LinkedList<Character> characterLinkedList, Context context) {
        mDataset = characterLinkedList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).bindData(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.fragment_letters;
    }
}
