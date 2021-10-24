package com.FIT3170.HealthMonitor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.FIT3170.HealthMonitor.adapters.NotificationHistoryAdapter;
import com.FIT3170.HealthMonitor.database.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query.Direction;

import java.util.ArrayList;
import java.util.Arrays;



/**
 * Fragment to display a users notification history. Retrieves it from the database and dislpays
 * in a list view
 */
public class NotificationHistory extends Fragment {


    private ArrayList<Notification> notificationArray;
    RecyclerView recyclerView;
    private NotificationHistoryAdapter adapter;



    public NotificationHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        notificationArray = new ArrayList<>();
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        //setContentView(R.layout.activity_main);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_history, container, false);
    }

    /**
     * Create reference to the Notification history adapter
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Getting reference of recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Setting the layout as linear
        // layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Sending reference and data to Adapter
        adapter = new NotificationHistoryAdapter(getActivity(), notificationArray);

        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);

    }

    /**
     * Call the database and refresh the notifications display with the new data
     * Once the new data has been pulled, notify the adapter the data has changed
     */
    private void overwriteNotifications(){
        // query firebase to get notification history
        FirebaseFirestore.getInstance().collection("patients")
                .document(UserProfile.getUid())
                .collection("notificationHistory")
                .orderBy("notificationTime", Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // If the notifications are pulled, clear the array and add the
                            // new notification objects
                            notificationArray.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("d", document.getId() + " => " + document.getData());
                                notificationArray.add(new Notification(
                                        (String) document.getData().get("notificationTitle"),
                                        (String) document.getData().get("notificationDescription"),
                                        (Timestamp) document.getData().get("notificationTime")));

                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("d", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Called everytime the fragment is opened
     * Fetches the notifications again
     */
    @Override
    public void onResume() {
        overwriteNotifications();
        super.onResume();
    }


}