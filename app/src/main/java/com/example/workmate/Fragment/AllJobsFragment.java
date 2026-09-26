package com.example.workmate.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.workmate.Adapter.JobAdapter;
import com.example.workmate.Model.Job;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllJobsFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView_all_jobs;
    private JobAdapter job_adapter;
    private List<Job> job_list;
    private ProgressBar progressBar;

    public AllJobsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_all_jobs, container, false);

         recyclerView_all_jobs = view.findViewById(R.id.recyclerView_all_jobs);
         progressBar = view.findViewById(R.id.progressBar_all_jobs);

         recyclerView_all_jobs.setLayoutManager(new LinearLayoutManager(getContext()));
         job_list = new ArrayList<>();
         job_adapter = new JobAdapter(getContext(),job_list,null,false,false,null);
         recyclerView_all_jobs.setAdapter(job_adapter);

         db = FirebaseFirestore.getInstance();

         load_all_jobs();


         return view;
    }

    private void load_all_jobs(){
        progressBar.setVisibility(View.INVISIBLE);

        db.collection("İlanlar").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    job_list.clear();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Job job = document.toObject(Job.class);
                        job_list.add(job);
                    }
                    job_adapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Veritabanına erişirken hata oluştu!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}