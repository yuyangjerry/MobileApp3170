package com.FIT3170.HealthMonitor.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FIT3170.HealthMonitor.Notification;
import com.FIT3170.HealthMonitor.R;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder> {
    ArrayList<Notification> notifications;
    Context context;

    // Constructor for initialization
    public NotificationHistoryAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        // Passing view to ViewHolder
        NotificationHistoryAdapter.ViewHolder viewHolder = new NotificationHistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull NotificationHistoryAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.title.setText((String) notifications.get(position).getTitle());
        holder.time.setText(notifications.get(position).formatTime());
        holder.description.setText(notifications.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return notifications.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView time;
        TextView description;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.courseName);
            time = view.findViewById(R.id.time);
            description = view.findViewById(R.id.description);

        }
    }

}