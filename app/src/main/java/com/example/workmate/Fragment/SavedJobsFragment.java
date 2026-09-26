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


public class SavedJobsFragment extends Fragment {

    private RecyclerView recyclerView_saved_jobs;
    private JobAdapter saved_job_adapter;
    private List<Job> saved_job_list;
    private TextView textView_no_saved_jobs;

    private FirebaseFirestore db;
    private FirebaseAuth auth;


    public SavedJobsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_saved_jobs, container, false);

         recyclerView_saved_jobs = view.findViewById(R.id.recyclerView_saved_jobs);
         textView_no_saved_jobs = view.findViewById(R.id.textView_no_saved_jobs);

         recyclerView_saved_jobs.setLayoutManager(new LinearLayoutManager(getContext()));
         saved_job_list = new ArrayList<>();
         saved_job_adapter = new JobAdapter(getContext(), saved_job_list, new JobAdapter.OnJobUpdated() {
             @Override
             public void onJobUpdated() {
                 load_saved_jobs();
             }
         }, true,false,null);

         recyclerView_saved_jobs.setAdapter(saved_job_adapter);

         db = FirebaseFirestore.getInstance();
         auth = FirebaseAuth.getInstance();

         load_saved_jobs();


         return view;
    }

    private void load_saved_jobs(){
        String userID = auth.getCurrentUser().getUid();
        


        db.collection("users").document(userID).collection("saved_jobs").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                saved_job_list.clear();
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                    Job job = document.toObject(Job.class);
                    saved_job_list.add(job);
                }
                saved_job_adapter.notifyDataSetChanged();
                // Eğer hiç kayıt yoksa textView göster
                if(saved_job_list.isEmpty()){
                    textView_no_saved_jobs.setVisibility(View.VISIBLE);
                }else{
                    textView_no_saved_jobs.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Kaydedilen ilanlar çekilemedi!", Toast.LENGTH_SHORT).show();
            }
        });



    }
}