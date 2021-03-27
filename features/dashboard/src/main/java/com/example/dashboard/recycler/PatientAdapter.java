package com.example.dashboard.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dashboard.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private ArrayList<PatientItem> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onPatientAddClick(int position, View v);
        void onPatientRemoveClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Class that is deals with the views inside the recycler.
     */
    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ImageView mPatientRemoveImage;
        public ImageView mPatientAddImage;

        public PatientViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);

            // add and remove btns
            mPatientRemoveImage = itemView.findViewById(R.id.image_patient_remove);
            mPatientAddImage = itemView.findViewById(R.id.image_patient_add);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mPatientRemoveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onPatientRemoveClick(position, itemView);
                        }
                    }
                }
            });

            mPatientAddImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onPatientAddClick(position, itemView);
                        }
                    }
                }
            });
        }
    }
    public PatientAdapter(ArrayList<PatientItem> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patients_item, parent, false);
        PatientViewHolder evh = new PatientViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        PatientItem currentItem = mExampleList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mTextView2.setText(currentItem.getmText2());
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
