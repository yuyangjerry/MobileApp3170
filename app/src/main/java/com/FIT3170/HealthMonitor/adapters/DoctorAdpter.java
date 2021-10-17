package com.FIT3170.HealthMonitor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FIT3170.HealthMonitor.Doctor;
import com.FIT3170.HealthMonitor.R;

import java.util.ArrayList;

public class DoctorAdpter extends RecyclerView.Adapter<DoctorAdpter.MyViewHolder> {

    private ArrayList<Doctor> doctorList;

    private RecyclerViewClickListener listener;

    public DoctorAdpter(ArrayList<Doctor> doctorList, RecyclerViewClickListener listener){
        this.doctorList = doctorList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView doctorId, doctorName;

        public MyViewHolder(final View view){
            super(view);
            doctorId = view.findViewById(R.id.textView9);
            doctorName = view.findViewById(R.id.textView8);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public DoctorAdpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        if(doctor != null){
            String docId = doctor.getDoctorID();
            holder.doctorId.setText(docId);
            String docName = doctor.getDoctorGivenName();
            holder.doctorName.setText(docName);
        }
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }
}
