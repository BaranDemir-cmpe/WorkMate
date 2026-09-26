package com.example.workmate.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workmate.Adapter.JobAdapter;
import com.example.workmate.Model.Job;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AppliedJobsFragment extends Fragment {

    private TextView textView_no_basvurulan_ilan;

    private RecyclerView recyclerView_applied_jobs;
    private JobAdapter applied_job_adapter;
    private List<Job> applied_job_list;


    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userID;


    public AppliedJobsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_applied_jobs, container, false);

        textView_no_basvurulan_ilan = view.findViewById(R.id.textView_no_basvurulan_ilan);


        recyclerView_applied_jobs = view.findViewById(R.id.recyclerView_applied_jobs);
        recyclerView_applied_jobs.setLayoutManager(new LinearLayoutManager(getContext()));

        applied_job_list = new ArrayList<>();
        applied_job_adapter = new JobAdapter(getContext(),applied_job_list,null,false,false,null);

        recyclerView_applied_jobs.setAdapter(applied_job_adapter);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        userID = auth.getCurrentUser().getUid();


        load_applied_jobs();


        return view;
    }

    private void load_applied_jobs(){

        db.collection("users").document(userID).collection("basvurulan_ilanlar").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                applied_job_list.clear();
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                    Job job = document.toObject(Job.class);
                    applied_job_list.add(job);
                }

                applied_job_adapter.notifyDataSetChanged();

                if(applied_job_list.isEmpty()){
                    textView_no_basvurulan_ilan.setVisibility(View.VISIBLE);
                }else{
                    textView_no_basvurulan_ilan.setVisibility(View.GONE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Veriler alınamadı! ",Toast.LENGTH_SHORT).show();
            }
        });
    }
}