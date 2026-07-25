package com.example.myfinalproject;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfinalproject.databinding.ItemTipBinding;
import java.util.ArrayList;
import java.util.List;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.ViewHolder>{
    private final ArrayList<String> tips=new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        ItemTipBinding binding=ItemTipBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){
        holder.bind(tips.get(position),position);
    }

    @Override
    public int getItemCount(){
        return tips.size();
    }

    public void setItems(List<String> newItems){
        tips.clear();
        tips.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemTipBinding binding;

        ViewHolder(ItemTipBinding binding){
            super(binding.getRoot());
            this.binding=binding;
        }

        void bind(String tip,int position){
            binding.tvTipText.setText(tip);
        }
    }
}
