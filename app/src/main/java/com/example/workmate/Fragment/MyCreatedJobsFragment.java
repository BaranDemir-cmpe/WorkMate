package com.example.workmate.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workmate.Adapter.JobAdapter;
import com.example.workmate.Model.Job;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyCreatedJobsFragment extends Fragment{

    private RecyclerView recyclerView_my_created_jobs;
    private List<Job> my_created_jobs_list;
    private JobAdapter adapter_my_created_jobs;
    private TextView textView_no_created_jobs;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_my_created_jobs, container, false);

         recyclerView_my_created_jobs = view.findViewById(R.id.recyclerView_my_created_jobs);
         recyclerView_my_created_jobs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        my_created_jobs_list = new ArrayList<>();

         adapter_my_created_jobs = new JobAdapter(requireContext(), my_created_jobs_list, new JobAdapter.OnJobUpdated() {
            @Override
            public void onJobUpdated() {
                // Fragment içinde doğrudan güncelleme
                loadCreatedJobs();
            }
        }, false, true, getParentFragmentManager());

         recyclerView_my_created_jobs.setAdapter(adapter_my_created_jobs);



         textView_no_created_jobs = view.findViewById(R.id.textView_no_created_jobs);

         db = FirebaseFirestore.getInstance();
         auth = FirebaseAuth.getInstance();


        loadCreatedJobs();



         return view;
    }

    private void loadCreatedJobs(){
        String userID = auth.getCurrentUser().getUid().trim();

        // UID'yi loglayalım
        Log.d("DEBUG_UID", "Current UID: " + userID);

        db.collection("İlanlar").whereEqualTo("userId",userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                my_created_jobs_list.clear();
                for(DocumentSnapshot document: queryDocumentSnapshots){
                    Job job = document.toObject(Job.class);
                    my_created_jobs_list.add(job);
                }
                if(my_created_jobs_list.isEmpty()){
                    textView_no_created_jobs.setVisibility(View.VISIBLE);
                    recyclerView_my_created_jobs.setVisibility(View.GONE);
                }else{
                    textView_no_created_jobs.setVisibility(View.GONE);
                    recyclerView_my_created_jobs.setVisibility(View.VISIBLE);
                }

                adapter_my_created_jobs.notifyDataSetChanged();
            }
        });
    }

}