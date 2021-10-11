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
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ArrayList<Notification> notificationArray;
    RecyclerView recyclerView;
    private NotificationHistoryAdapter adapter;

    // Using ArrayList to store images data

    ArrayList<String> courseName = new ArrayList<>(Arrays.asList("Software Update Available",
            "Abnormal Heart Rate Detected: Contact GP", "Heartsense at Full Charge",
            "Heartsense Disconnected", "Heartsense at 15% Charge", "New Doctor Successfully linked"
            , "Software Update Successful"));


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationHistory newInstance(String param1, String param2) {
        NotificationHistory fragment = new NotificationHistory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
     */
    private void overwriteNotifications(){
        // TODO: Refactor this into the User Profile class?
        FirebaseFirestore.getInstance().collection("patients")
                .document(UserProfile.getUid())
                .collection("notificationHistory")
                .orderBy("notificationTime", Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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

    @Override
    public void onResume() {
        overwriteNotifications();
        super.onResume();
    }


}