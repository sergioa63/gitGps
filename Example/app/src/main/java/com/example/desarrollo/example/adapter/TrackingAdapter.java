package com.example.desarrollo.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.desarrollo.example.R;
import com.library.modulo.core.db.database.gpsChip.Gps;


import java.util.List;


public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder> {

    private Context context;
    private List<Gps> data;

    public TrackingAdapter(Context context, List<Gps> listGps){
        this.context = context;
        this.data = listGps;
    }

    @Override
    public TrackingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tracking, parent, false);
        return new TrackingViewHolder(row);
    }

    @Override
    public void onBindViewHolder(TrackingViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Gps gps = data.get(position);
        holder.txtFecha.setText(gps.getDate());
        holder.txtAltitud.setText(String.valueOf(gps.getAccuracy()));
        holder.txtLatitud.setText(String.valueOf(gps.getLatitude()));
        holder.txtLongitud.setText(String.valueOf(gps.getLongitude()));
        holder.txtProveedor.setText(String.valueOf(gps.getProvider()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItems(List<Gps> list){
        this.data = list;
    }

    class TrackingViewHolder extends RecyclerView.ViewHolder {

        TextView txtFecha;
        TextView txtAltitud;
        TextView txtLatitud;
        TextView txtLongitud;
        TextView txtProveedor;

        public TrackingViewHolder(View itemView) {
            super(itemView);

            txtFecha = (TextView) itemView.findViewById(R.id.txtDate);
            txtAltitud = (TextView) itemView.findViewById(R.id.txtAltitude);
            txtLatitud = (TextView) itemView.findViewById(R.id.txtLatitude);
            txtLongitud = (TextView) itemView.findViewById(R.id.txtLongitude);
            txtProveedor = (TextView) itemView.findViewById(R.id.txtProveedor);
        }
    }
}
