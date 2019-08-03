package com.project.smartbus10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class BusStopAdapter extends ArrayAdapter<BusStop> {
    private Context context;

    public BusStopAdapter(@NonNull Context context, int resource, List<BusStop> busStops) {
        super(context, resource, busStops);
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup ViewParent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.bus_item, ViewParent, false);
        }
        RelativeLayout layout = convertView.findViewById(R.id.busitem);
        ImageView image = (ImageView) convertView.findViewById(R.id.profile_image);
        TextView nameText = (TextView) convertView.findViewById(R.id.name_text_view);
        TextView text = (TextView) convertView.findViewById(R.id.stateText);
        ImageView state = (ImageView) convertView.findViewById(R.id.state);

        final BusStop busStop = getItem(position);
        image.setImageResource(R.drawable.bus_stops_icon);
        if(busStop!=null){
        if(busStop.getBusStopAddress().length()>15)
        { nameText.setText(busStop.getBusStopAddress().substring(0, busStop.getBusStopAddress().length() / 2) + "\n" + busStop.getBusStopAddress().substring(busStop.getBusStopAddress().length() / 2));}
          else{nameText.setText(busStop.getBusStopAddress());}
        nameText.setTextSize(14);}
        if (busStop.getBusStopLatitude() != 0 || busStop.getBusStopAddress() != null) {
            state.setImageResource(R.drawable.location);
        }
        text.setText(busStop.getBusStopOrder()+"");
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap = new Intent(context, BusStopsMapsActivity.class);
                goToMap.putExtra("latitude", busStop.getBusStopLatitude());
                goToMap.putExtra("longitude", busStop.getBusStopLongitude());
                goToMap.putExtra("stopName", busStop.getBusStopAddress());
                goToMap.putExtra("createMarker", false);
                context.startActivity(goToMap);
            }
        });

        return convertView;
    }

}