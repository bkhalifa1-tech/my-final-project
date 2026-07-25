package com.example.myfinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfinalproject.databinding.ItemAppointmentBinding;
import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder>{
    public interface OnAppointmentClickListener{
        void onAppointmentClick(Appointment appointment);
    }

    private final ArrayList<Appointment> appointments=new ArrayList<>();
    private final OnAppointmentClickListener listener;

    public AppointmentAdapter(OnAppointmentClickListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        ItemAppointmentBinding binding=ItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        holder.bind(appointments.get(position));
    }

    @Override
    public int getItemCount(){
        return appointments.size();
    }

    public void setItems(List<Appointment> newItems){
        appointments.clear();
        appointments.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(Appointment appointment){
        appointments.add(appointment);
        notifyItemInserted(appointments.size()-1);
    }

    public void updateItem(Appointment appointment){
        for(int i=0;i<appointments.size();i++){
            if(appointments.get(i).getId()==appointment.getId()){
                appointments.set(i,appointment);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeItem(long appointmentId){
        for(int i=0;i<appointments.size();i++){
            if(appointments.get(i).getId()==appointmentId){
                appointments.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemAppointmentBinding binding;

        ViewHolder(ItemAppointmentBinding binding){
            super(binding.getRoot());
            this.binding=binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onAppointmentClick(appointments.get(position));
                    }
                }
            });
        }
        void bind(Appointment appointment){
            binding.tvItemTitle.setText(appointment.getTitle());
            binding.tvItemTime.setText(appointment.getDate()+" - "+appointment.getTime());
        }
    }
}
