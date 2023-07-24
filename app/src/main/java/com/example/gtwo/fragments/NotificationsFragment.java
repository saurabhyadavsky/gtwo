package com.example.gtwo.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.gtwo.R;
import com.example.gtwo.adapters.AdapterNotification;
import com.example.gtwo.models.ModelNotifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {


    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    ArrayList<ModelNotifications> modelNotifications;
    AdapterNotification adapterNotification;
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notifications, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.notificationdata);
        getAllNotification();
        return view;
    }

    private void getAllNotification() {
        modelNotifications=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelNotifications.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            ModelNotifications notifications=dataSnapshot1.getValue(ModelNotifications.class);
                            modelNotifications.add(notifications);
                        }
                        adapterNotification=new AdapterNotification(getActivity(),modelNotifications);
                        recyclerView.setAdapter(adapterNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
