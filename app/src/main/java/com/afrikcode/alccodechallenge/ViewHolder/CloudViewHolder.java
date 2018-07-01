package com.afrikcode.alccodechallenge.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afrikcode.alccodechallenge.Interface.ItemClickListener;
import com.afrikcode.alccodechallenge.R;

public class CloudViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvTitle, tvBody, tvDate, tvKey;

    private ItemClickListener itemClickListener;

    public CloudViewHolder(View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvBody = itemView.findViewById(R.id.tvBody);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvKey = itemView.findViewById(R.id.key);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
