package com.example.workmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workmate.Fragment.ApplicantsFragment;
import com.example.workmate.Model.Job;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.MyViewHolder> {

    private Context context;
    private List<Job> jobList;
    private OnJobUpdated jobUpdatedListener;
    private boolean isSavedList;
    private boolean isJobSaved = false;
    private boolean isInMyCreatedJobsFragment;
    private FragmentManager fragmentManager;

    public interface OnJobUpdated{
        void onJobUpdated();
    }


    public JobAdapter(Context context,List<Job> jobList,OnJobUpdated jobUpdatedListener,boolean isSavedList,boolean isInMyCreatedJobsFragment,FragmentManager fragmentManager){
        this.context = context;
        this.jobList = jobList;
        this.jobUpdatedListener = jobUpdatedListener;
        this.isSavedList = isSavedList;
        this.isInMyCreatedJobsFragment = isInMyCreatedJobsFragment;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public JobAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.MyViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.txt_job_title.setText(job.getJobTitle());
        holder.txt_company.setText(job.getCompany());
        holder.txt_location.setText(job.getLocation());
        holder.txt_job_type.setText(job.getJobType());
        holder.txt_location_type.setText(job.getLocationType());
        holder.txt_salary_min.setText(String.valueOf(job.getMinSalary()));
        holder.txt_salary_max.setText(String.valueOf(job.getMaxSalary()));
        holder.txt_currency.setText(job.getCurrency());
        holder.txt_currency_2.setText(job.getCurrency());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInMyCreatedJobsFragment){
                    // MyCreatedJobs Fragmenttaysa yeni fragment a geç ve başvuranları göster
                    ApplicantsFragment fragment = new ApplicantsFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("jobID",job.getJobID());
                    fragment.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

                }else{
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_job_details,null);

                    ((TextView) view.findViewById(R.id.txtJobTitle)).setText(job.getJobTitle());
                    ((TextView) view.findViewById(R.id.txtCompany)).setText(job.getCompany());
                    ((TextView) view.findViewById(R.id.txtLocation)).setText(job.getLocation());
                    ((TextView) view.findViewById(R.id.txtLocationType)).setText(job.getJobType());
                    ((TextView) view.findViewById(R.id.txtSalary)).setText(String.valueOf(job.getMinSalary()) + " - " + String.valueOf(job.getMaxSalary()) + " " + job.getCurrency());
                    ((TextView) view.findViewById(R.id.txtDescription)).setText(job.getDescription());
                    ((TextView) view.findViewById(R.id.txtRequirements)).setText(job.getRequirements());
                    ((TextView) view.findViewById(R.id.txtContactEmail)).setText(job.getContactEmail());
                    ((TextView) view.findViewById(R.id.txtContactPhone)).setText(job.getContactPhone());

                    MaterialButton btn_apply = view.findViewById(R.id.btnApply);

                    // BottomSheet açılır açılmaz başvuru durumu kontrolü

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String userID = auth.getCurrentUser().getUid();

                    db.collection("users").document(userID).collection("basvurulan_ilanlar")
                            .whereEqualTo("jobID",job.getJobID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(!task.getResult().isEmpty()){
                                            // Zaten başvurulmuş
                                            btn_apply.setText("Başvuruldu");
                                            btn_apply.setEnabled(false);
                                            btn_apply.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey));
                                        }else{
                                            // Başvurmamışsa
                                            btn_apply.setText("Başvur");
                                            btn_apply.setEnabled(true);

                                            btn_apply.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Map<String,Object> appliedJob = new HashMap<>();
                                                    appliedJob.put("jobID", job.getJobID());
                                                    appliedJob.put("jobTitle", job.getJobTitle());
                                                    appliedJob.put("company", job.getCompany());
                                                    appliedJob.put("location", job.getLocation());
                                                    appliedJob.put("jobType", job.getJobType());
                                                    appliedJob.put("locationType", job.getLocationType());
                                                    appliedJob.put("minSalary", job.getMinSalary());
                                                    appliedJob.put("maxSalary", job.getMaxSalary());
                                                    appliedJob.put("currency", job.getCurrency());
                                                    appliedJob.put("description", job.getDescription());
                                                    appliedJob.put("requirements", job.getRequirements());
                                                    appliedJob.put("contactEmail", job.getContactEmail());
                                                    appliedJob.put("contactPhone", job.getContactPhone());

                                                    db.collection("users").document(userID).collection("basvurulan_ilanlar").add(appliedJob).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            // İlanın "applicants" listesine UserID ekleme
                                                            DocumentReference ilanRef = db.collection("İlanlar").document(job.getDocumentId());
                                                            ilanRef.update("applicants", FieldValue.arrayUnion(userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    // Başarılı şekilde eklendi
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(context,"Başvuru listesine eklenemedi!",Toast.LENGTH_SHORT).show();
                                                                }
                                                            });


                                                            Toast.makeText(context, "Başvurun Alındı ve Kaydedildi!", Toast.LENGTH_SHORT).show();

                                                            // Başarı sonrası buton değişimi
                                                            btn_apply.setText("Başvuruldu");
                                                            btn_apply.setEnabled(false);
                                                            btn_apply.setBackgroundColor(ContextCompat.getColor(context,R.color.darkGrey));

                                                            // 1 Saniye sonra dialog kapatma
                                                            new android.os.Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    bottomSheetDialog.dismiss();

                                                                    if (jobUpdatedListener != null){
                                                                        jobUpdatedListener.onJobUpdated();
                                                                    }
                                                                }
                                                            },1000);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Başvuru kaydedilemedi.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }else{
                                        Toast.makeText(context, "Başvuru durumu kontrol edilemedi.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    bottomSheetDialog.setContentView(view);
                    bottomSheetDialog.show();
                }
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();

        // Firestore dan saved_jobs koleksiyonunda bu ilan var mı kontrol et
        db.collection("users").document(userID).collection("saved_jobs").whereEqualTo("jobID",job.getJobID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    // zaten kaydedilmiş
                    holder.bookmark_icon.setImageResource(R.drawable.bookmark_saved);
                    holder.bookmark_icon.setTag("saved");
                }else{
                    // henüz kaydedilmemiş
                    holder.bookmark_icon.setImageResource(R.drawable.bookmark);
                    holder.bookmark_icon.setTag("unsaved");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Kaydedilen ilanlar alınamadı.", Toast.LENGTH_SHORT).show();
            }
        });


        // Bookmark tıklanınca
        holder.bookmark_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Tıklanınca animasyonu başlat
                Animation scale_pop = AnimationUtils.loadAnimation(context,R.anim.scale_pop);
                holder.bookmark_icon.startAnimation(scale_pop);

                Object tag = holder.bookmark_icon.getTag();
                if(tag !=null && tag.equals("saved")){
                    // Eğer kaydedilmişse --> kaldır
                    db.collection("users").document(userID).collection("saved_jobs")
                            .whereEqualTo("jobID",job.getJobID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(DocumentSnapshot document : queryDocumentSnapshots){
                                        document.getReference().delete();
                                    }
                                    Toast.makeText(context, "İlan kaydedilenlerden kaldırıldı.", Toast.LENGTH_SHORT).show();
                                    holder.bookmark_icon.setImageResource(R.drawable.bookmark); // ikon boş yap
                                    holder.bookmark_icon.setTag("unsaved"); // tag'ı değiştiriyoruz


                                    if (jobUpdatedListener != null) {
                                        jobUpdatedListener.onJobUpdated();
                                    }
                                }
                            });
                }else{
                    // Eğer kaydedilmemişse --> kaydet
                    Map<String,Object> savedJob = new HashMap<>();
                    savedJob.put("jobID", job.getJobID());
                    savedJob.put("jobTitle", job.getJobTitle());
                    savedJob.put("company", job.getCompany());
                    savedJob.put("location", job.getLocation());
                    savedJob.put("jobType", job.getJobType());
                    savedJob.put("locationType", job.getLocationType());
                    savedJob.put("minSalary", job.getMinSalary());
                    savedJob.put("maxSalary", job.getMaxSalary());
                    savedJob.put("currency", job.getCurrency());
                    savedJob.put("description", job.getDescription());
                    savedJob.put("requirements", job.getRequirements());
                    savedJob.put("contactEmail", job.getContactEmail());
                    savedJob.put("contactPhone", job.getContactPhone());

                    db.collection("users").document(userID).collection("saved_jobs").add(savedJob).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "İlan kaydedildi!", Toast.LENGTH_SHORT).show();
                            holder.bookmark_icon.setImageResource(R.drawable.bookmark_saved); // ikon dolu yap
                            holder.bookmark_icon.setTag("saved"); // tag'ı değiştiriyoruz


                            if (jobUpdatedListener != null) {
                                jobUpdatedListener.onJobUpdated();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_job_title, txt_company, txt_location,txt_job_type,txt_location_type,txt_salary_min,txt_salary_max,txt_currency,txt_currency_2;
        public ImageView bookmark_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_job_title = itemView.findViewById(R.id.txtJobTitle);
            txt_company = itemView.findViewById(R.id.txtCompany);
            txt_location = itemView.findViewById(R.id.txtLocation);
            txt_job_type = itemView.findViewById(R.id.txtJobType);
            txt_location_type = itemView.findViewById(R.id.txtLocationType);
            txt_salary_min = itemView.findViewById(R.id.txtSalary_Min);
            txt_salary_max = itemView.findViewById(R.id.txtSalary_Max);
            txt_currency = itemView.findViewById(R.id.txt_Currency);
            txt_currency_2 = itemView.findViewById(R.id.txt_Currency_2);
            bookmark_icon = itemView.findViewById(R.id.bookmark_icon);


        }


    }
}
