package com.example.workmate.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.workmate.Adapter.UserAdapter;
import com.example.workmate.Model.UserProfile;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ApplicantsFragment extends Fragment {


    public ApplicantsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView_applicants;
    private List<UserProfile> userList;
    private UserAdapter userAdapter;
    private String jobID;
    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_applicants, container, false);

        recyclerView_applicants = view.findViewById(R.id.recyclerView_applicants);
        recyclerView_applicants.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        userList = new ArrayList<>();

        userAdapter = new UserAdapter(userList,getContext());
        recyclerView_applicants.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();

        if(jobID != null){
            db.collection("İlanlar").document(jobID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        List<String> applicantIds = (List<String>) documentSnapshot.get("applicants");

                        if(applicantIds == null || applicantIds.isEmpty()){
                            Toast.makeText(getContext(),"Bu ilana henüz başvuran yok.",Toast.LENGTH_SHORT).show();
                        }

                        if(applicantIds != null && !applicantIds.isEmpty()){
                            for(String userID: applicantIds){
                                db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            UserProfile user = documentSnapshot.toObject(UserProfile.class);
                                            user.setUserId(documentSnapshot.getId());// burası yeni kısım
                                            userList.add(user);
                                            userAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }


        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            jobID = getArguments().getString("jobID");
        }
    }












}

