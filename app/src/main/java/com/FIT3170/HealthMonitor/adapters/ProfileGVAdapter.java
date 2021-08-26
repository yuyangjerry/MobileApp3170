package com.FIT3170.HealthMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.FIT3170.HealthMonitor.ProfileAttributeModel;
import com.FIT3170.HealthMonitor.R;

import java.util.ArrayList;

public class ProfileGVAdapter extends ArrayAdapter<ProfileAttributeModel> {
    public ProfileGVAdapter(@NonNull Context context, ArrayList<ProfileAttributeModel> profileModelArrayList) {
        super(context, 0, profileModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }
        ProfileAttributeModel profileModel = getItem(position);
        TextView attribute = listitemView.findViewById(R.id.profileAttribute);
        TextView value = listitemView.findViewById(R.id.profileValue);

        attribute.setText(profileModel.getAttribute_name());
        value.setText(profileModel.getAttribute_value());

        return listitemView;
    }
}