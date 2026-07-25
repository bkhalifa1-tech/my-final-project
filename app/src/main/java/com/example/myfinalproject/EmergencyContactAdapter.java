package com.example.myfinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfinalproject.databinding.ItemNumberBinding;
import java.util.ArrayList;
import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder>{
    public interface OnContactActionListener{
        void onCall(EmergencyContact contact);
        void onDelete(EmergencyContact contact,int position);
    }

    private final ArrayList<EmergencyContact> contacts=new ArrayList<>();
    private final OnContactActionListener listener;

    public EmergencyContactAdapter(OnContactActionListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        ItemNumberBinding binding=ItemNumberBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        holder.bind(contacts.get(position));
    }

    @Override
    public int getItemCount(){
        return contacts.size();
    }

    public void setItems(List<EmergencyContact> newItems){
        contacts.clear();
        contacts.addAll(newItems);
        notifyDataSetChanged();
    }

    public void addItem(EmergencyContact contact){
        contacts.add(0,contact);
        notifyItemInserted(0);
    }

    public void removeItem(int position){
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemNumberBinding binding;

        ViewHolder(ItemNumberBinding binding){
            super(binding.getRoot());
            this.binding=binding;

            binding.vTphoneCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCall(contacts.get(position));
                    }
                }
            });

            binding.vTDeleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDelete(contacts.get(position), position);
                    }
                }
            });
        }
        void bind(EmergencyContact contact){
            binding.numberNameTv.setText(contact.getContactName());
            binding.numberPhoneTv.setText(contact.getPhoneNumber());
        }
    }
}
