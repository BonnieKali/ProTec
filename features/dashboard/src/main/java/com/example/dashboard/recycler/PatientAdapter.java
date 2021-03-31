package com.example.dashboard.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dashboard.R;
import com.example.session.Session;
import com.example.session.user.UserSession;
import com.example.session.user.carer.CarerSession;

import java.util.ArrayList;
import java.util.Collections;

import androidx.recyclerview.widget.RecyclerView;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private ArrayList<PatientItem> patientsList;
    private OnItemClickListener mListener;
    private Context context;    // used for getting colours

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
        public FrameLayout cardBackground;

        public PatientViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            cardBackground = itemView.findViewById(R.id.patient_card_frame_layout);

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
    public PatientAdapter(ArrayList<PatientItem> patientsList, Context context ) {
        this.patientsList = patientsList;
        this.context = context;
    }

    /**
     * Create the cardviewers
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patients_item, parent, false);
        PatientViewHolder evh = new PatientViewHolder(v, mListener);
        return evh;
    }

    /**
     * This where all the data is loaded into the card viewer, this is also called
     * when adapter.notifyItemChanged is called
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        PatientItem currentItem = patientsList.get(position);
        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mTextView2.setText(currentItem.getmText2());
        setUpPatient(currentItem, holder);
    }

    /**
     * This sets up how the patientItem should look such as colouring the background green
     * if the patient belongs to the carer
     * @param currentItem
     * @param holder
     */
    private void setUpPatient(PatientItem currentItem, PatientViewHolder holder){
        // set background colour along with removing btns to add and remove
        CarerSession user = (CarerSession) Session.getInstance().getUser(); // we know carer is loading this
        if (currentItem.isBelongToCarer(user)){
            holder.cardBackground.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            holder.mPatientAddImage.setVisibility(View.INVISIBLE);
            holder.mPatientRemoveImage.setVisibility(View.VISIBLE);
        }else{
            holder.cardBackground.setBackgroundColor(context.getResources().getColor(R.color.light_red));
            holder.mPatientAddImage.setVisibility(View.VISIBLE);
            holder.mPatientRemoveImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }
}
