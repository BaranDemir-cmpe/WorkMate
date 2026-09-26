package com.example.workmate.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workmate.Activity.UserListActivity;
import com.example.workmate.Adapter.JobAdapter;
import com.example.workmate.Model.Job;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView_ilanlar,recyclerView_basvurulan_ilanlar;
    private JobAdapter job_adapter,basvurulan_adapter;
    private List<Job> jobList,applied_job_list;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Spinner spinner;
    private ProgressBar progressBar1, progressBar2;
    private TextView textView_basvuru_yok,textView_see_all_1,textView_onerilen_ilan_yok,textView_user_name,textView_see_all_applied_jobs;
    private String userID;
    private ImageView imageView_filter,profile_picture, imageView_messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        textView_basvuru_yok = view.findViewById(R.id.textView_basvuru_yok);
        textView_see_all_1 = view.findViewById(R.id.textView_see_all_1);
        textView_onerilen_ilan_yok = view.findViewById(R.id.textView_onerilen_ilan_yok);
        textView_user_name = view.findViewById(R.id.home_fragment_user_name);
        profile_picture = view.findViewById(R.id.main_fragment_profile_picture);
        imageView_messages = view.findViewById(R.id.messages_imageView);
        textView_see_all_applied_jobs = view.findViewById(R.id.textView_see_all_applied_jobs);

        // ImageView_filter
        imageView_filter = view.findViewById(R.id.imageView_filter);
        imageView_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_bottom_sheet_filter();
            }
        });

        imageView_messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserListActivity.class);
                startActivity(intent);
            }
        });

        // Profil resmi ve kullanıcı ismini veritabanından çekildiği kısım
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("name");
                    String imageUrl = documentSnapshot.getString("profilResimleriUrl");
                    if(name != null && !name.isEmpty()){
                        textView_user_name.setText(name);
                    }
                    if(imageUrl != null && !imageUrl.isEmpty()){
                        Glide.with(getContext()).load(imageUrl).into(profile_picture);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"İsim veya profil resmi alınamadı." + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



        // Progress Bar
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);

        // Tüm iş ilanları için recyclerView Yapısı

        recyclerView_ilanlar = view.findViewById(R.id.recyclerView_onerilen_isler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView_ilanlar.setLayoutManager(layoutManager);

        jobList = new ArrayList<>();

        job_adapter = new JobAdapter(getContext(), jobList, new JobAdapter.OnJobUpdated() {
            @Override
            public void onJobUpdated() {
                applied_job_list.clear();
                applied_jobs_fetch_from_fireStore();
            }
        },false,false,null);

        recyclerView_ilanlar.setAdapter(job_adapter);
        // Tam olarak recyclerview ın sağa sola geçişi sağlamak için gerekli olan kod
        SnapHelper pagerSnapHelper1 = new PagerSnapHelper();
        pagerSnapHelper1.attachToRecyclerView(recyclerView_ilanlar);

        // Basvurulan İşler için recyclerView yapısı
        recyclerView_basvurulan_ilanlar = view.findViewById(R.id.recyclerView_basvurulan_isler);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView_basvurulan_ilanlar.setLayoutManager(layoutManager2);
        applied_job_list = new ArrayList<>();
        basvurulan_adapter = new JobAdapter(getContext(), applied_job_list, new JobAdapter.OnJobUpdated() {
            @Override
            public void onJobUpdated() {
                fetch_from_fireStore();
            }
        }, false,false,null);
        recyclerView_basvurulan_ilanlar.setAdapter(basvurulan_adapter);

        // Tam olarak recyclerview ın sağa sola geçişi sağlamak için gerekli olan kod
        SnapHelper pagerSnapHelper2 = new PagerSnapHelper();
        pagerSnapHelper2.attachToRecyclerView(recyclerView_basvurulan_ilanlar);

        applied_jobs_fetch_from_fireStore(); // Basvurulan işleri firestore dan çekiyoruz
        if(applied_job_list.size()>0){
            progressBar2.setVisibility(View.INVISIBLE);
        }


        fetch_from_fireStore();

        // "Hepsini Gör" e tıklanınca yeni fragment a geçme
        textView_see_all_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment allJobsFragment = new AllJobsFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,allJobsFragment)
                        .addToBackStack(null).commit();
            }
        });

        textView_see_all_applied_jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment AppliedJobsFragment = new AppliedJobsFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,AppliedJobsFragment)
                        .addToBackStack(null).commit();
            }
        });




        return view;
    }

    private void open_bottom_sheet_filter(){
        final BottomSheetDialog bottom_sheet_filter = new BottomSheetDialog(getContext());
        View BottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_filter,null);
        bottom_sheet_filter.setContentView(BottomSheetView);
        bottom_sheet_filter.show();

        // Burada firestore dan verileri çekip spinnerları dolduruyoruz
        setup_spinners(BottomSheetView,bottom_sheet_filter);

    }

    private void setup_spinners(View BottomSheetView,final BottomSheetDialog bottomSheetDialog){
        Spinner spinnerLocation = BottomSheetView.findViewById(R.id.spinnerLocation);
        Spinner spinnerJobTitle = BottomSheetView.findViewById(R.id.spinnerJobTitle);
        Spinner spinnerJobType = BottomSheetView.findViewById(R.id.spinnerJobType);
        Spinner spinnerLocationType = BottomSheetView.findViewById(R.id.spinnerLocationType);
        Spinner spinnerCurrency = BottomSheetView.findViewById(R.id.spinnerCurrency);
        EditText EditText_MinSalary_filter = BottomSheetView.findViewById(R.id.EditText_MinSalary_filter);
        EditText EditText_MaxSalary_filter = BottomSheetView.findViewById(R.id.EditText_MaxSalary_filter);
        Button buttonShowResults = BottomSheetView.findViewById(R.id.buttonShowResults);

        List<String> locations = new ArrayList<>();
        List<String> jobTitles = new ArrayList<>();
        List<String> jobTypes = new ArrayList<>();
        List<String> locationTypes = new ArrayList<>();
        List<String> currencies = new ArrayList<>();



        locations.add("Seçiniz");
        jobTitles.add("Seçiniz");
        jobTypes.add("Seçiniz");
        locationTypes.add("Seçiniz");
        currencies.add("Seçiniz");

        db.collection("İlanlar").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                    String location = document.getString("location");
                    String jobTitle = document.getString("jobTitle");
                    String jobType = document.getString("jobType");
                    String locationType = document.getString("locationType");
                    String currency = document.getString("currency");

                    if(location != null && !locations.contains(location)) locations.add(location);
                    if(jobTitle != null && !jobTitles.contains(jobTitle)) jobTitles.add(jobTitle);
                    if(jobType != null && !jobTypes.contains(jobType)) jobTypes.add(jobType);
                    if(locationType != null && !locationTypes.contains(locationType)) locationTypes.add(locationType);
                    if(currency != null && !currencies.contains(currency)) currencies.add(currency);
                }

                // Spinnerları doldur
                ArrayAdapter<String> adapterLocation = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,locations);
                adapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocation.setAdapter(adapterLocation);

                ArrayAdapter<String> adapterJobTitle = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,jobTitles);
                adapterJobTitle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerJobTitle.setAdapter(adapterJobTitle);

                ArrayAdapter<String> adapterJobType = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,jobTypes);
                adapterJobType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerJobType.setAdapter(adapterJobType);

                ArrayAdapter<String> adapterlocationType = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,locationTypes);
                adapterlocationType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocationType.setAdapter(adapterlocationType);

                ArrayAdapter<String> adapterCurrency = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,currencies);
                adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCurrency.setAdapter(adapterCurrency);


                buttonShowResults.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Spinnerlardaki değerleri alıyoruz
                        String location = spinnerLocation.getSelectedItem().toString();
                        String jobTitle = spinnerJobTitle.getSelectedItem().toString();
                        String jobType = spinnerJobType.getSelectedItem().toString();
                        String locationType = spinnerLocationType.getSelectedItem().toString();
                        String currency = spinnerCurrency.getSelectedItem().toString();
                        String minSalaryText = EditText_MinSalary_filter.getText().toString();
                        String maxSalaryText = EditText_MaxSalary_filter.getText().toString();

                        Integer minSalary = null;
                        Integer maxSalary = null;

                        if(location.equals("Seçiniz")) location = null;
                        if(jobTitle.equals("Seçiniz")) jobTitle = null;
                        if(jobType.equals("Seçiniz")) jobType = null;
                        if(locationType.equals("Seçiniz")) locationType = null;
                        if(currency.equals("Seçiniz")) currency = null;

                        try{
                            if(!minSalaryText.isEmpty()){
                                minSalary = Integer.parseInt(minSalaryText);
                            }
                            if(!maxSalaryText.isEmpty()){
                                maxSalary = Integer.parseInt(maxSalaryText);
                            }
                        }catch (NumberFormatException e){
                            Toast.makeText(getContext(),"Lütfen geçerli bir maaş değeri giriniz.",Toast.LENGTH_SHORT).show();
                            return; // işlemi durdur
                        }


                        // Filtrelemeyi uygulayan methodu çağırıyoruz
                        apply_filters(location,jobTitle,jobType,locationType,currency,minSalary,maxSalary);

                        bottomSheetDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Spinner verileri çekilemedi.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apply_filters(String location, String jobTitle, String jobType, String locationType, String currency,Integer minSalary, Integer maxSalary){
        Query query = db.collection("İlanlar");

        //Filtreleri uyguluyoruz
        if(location != null && !location.isEmpty()){
            query = query.whereEqualTo("location",location);
        }
        if(jobTitle != null && !jobTitle.isEmpty()){
            query = query.whereEqualTo("jobTitle",jobTitle);
        }
        if(jobType != null && !jobType.isEmpty()) {
            query = query.whereEqualTo("jobType", jobType);
        }
        if(locationType != null && !locationType.isEmpty()) {
            query = query.whereEqualTo("locationType", locationType);
        }
        if(currency != null && !currency.isEmpty()) {
            query = query.whereEqualTo("currency", currency);
        }


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                jobList.clear();

                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getContext(),"Filtreye uygun iş ilanı bulunamadı.",Toast.LENGTH_SHORT).show();
                    progressBar1.setVisibility(View.GONE);
                    job_adapter.notifyDataSetChanged();
                    return;
                }

                List<Job> filteredJobs = new ArrayList<>();

                for(DocumentSnapshot document : queryDocumentSnapshots){
                    Job job = document.toObject(Job.class);
                    job.setDocumentId(document.getId());

                    // maaş filtresi uygulama
                    boolean salaryFilterPassed = true;

                    if(job !=null){
                        Integer jobMinSalary = job.getMinSalary();
                        Integer jobMaxSalary = job.getMaxSalary();

                        // Maaş Aralığı filtreleme
                        if(minSalary != null || maxSalary != null){
                            // Eğer ilanda maaş bilgisi yoksa ve kullanıcı maaş filtresi belirlemişse
                            if(jobMinSalary == null && jobMaxSalary == null){
                                salaryFilterPassed = false;
                            }else{
                                // Min maaş kontrolü - kullanıcının istediği min maaş ilanın max maaşından büyükse
                                if(minSalary != null && jobMaxSalary != null && minSalary > jobMaxSalary){
                                    salaryFilterPassed = false;
                                }

                                // Max maaş kontrolü - kullanıcının istediği max maaş, ilanın min küçükse
                                if(maxSalary != null && jobMinSalary != null && maxSalary < jobMinSalary){
                                    salaryFilterPassed = false;
                                }
                            }
                        }

                        // Filtreyi geçtiyse ekle
                        if(salaryFilterPassed){
                            filteredJobs.add(job);

                            String jobID = job.getJobID();

                            // kaydetme durumunu kontrol et
                            db.collection("users").document(userID).collection("saved_jobs").whereEqualTo("jobID",jobID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        job.setSaved(true);
                                    }else{
                                        job.setSaved(false);
                                    }
                                    job_adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                }
                // filtrelenmiş listeyi ana listeye ekle
                jobList.addAll(filteredJobs);
                progressBar1.setVisibility(View.GONE);

                //Filtreleme sonucunu göster
                Toast.makeText(getContext(),jobList.size() + "iş ilanı bulundu.",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar1.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Filtreleme sırasında hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetch_from_fireStore(){
        db.collection("İlanlar").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                jobList.clear();
                for(DocumentSnapshot document: queryDocumentSnapshots){
                    Job job = document.toObject(Job.class);
                    job.setDocumentId(document.getId());

                    if(job != null){
                        String jobID = job.getJobID();

                        // ilanı aldık, kullanıcı kaydetmiş mi kontrol et
                        db.collection("users").document(userID).collection("saved_jobs")
                                .whereEqualTo("jobID",jobID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()){
                                    job.setSaved(true); // kaydedilmiş
                                }else{
                                    job.setSaved(false); // kaydedilmemiş
                                }
                                jobList.add(job);
                                job_adapter.notifyDataSetChanged();

                                if(jobList.isEmpty()){
                                    recyclerView_ilanlar.setVisibility(View.GONE);
                                    textView_onerilen_ilan_yok.setVisibility(View.VISIBLE);
                                }else {
                                    recyclerView_ilanlar.setVisibility(View.VISIBLE);
                                    textView_onerilen_ilan_yok.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
                progressBar1.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Veriler alınamadı.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applied_jobs_fetch_from_fireStore(){
        String userID = auth.getCurrentUser().getUid();
        db.collection("users").document(userID).collection("basvurulan_ilanlar").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots){
                    Job job = document.toObject(Job.class);
                    job.setDocumentId(document.getId());
                    applied_job_list.add(job);
                }
                basvurulan_adapter.notifyDataSetChanged();

                progressBar2.setVisibility(View.GONE);

                if(applied_job_list.isEmpty()){
                    recyclerView_basvurulan_ilanlar.setVisibility(View.GONE);
                    textView_basvuru_yok.setVisibility(View.VISIBLE);
                }else{
                    recyclerView_basvurulan_ilanlar.setVisibility(View.VISIBLE);
                    textView_basvuru_yok.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Başvurulan İşler Çekilemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

