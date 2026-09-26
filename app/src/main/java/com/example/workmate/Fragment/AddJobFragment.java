package com.example.workmate.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.workmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class AddJobFragment extends Fragment {
    private EditText job_title_edit_text, company_edit_text, location_edit_text, min_salary_edit_text, max_salary_edit_text,
    description_edit_text, requirements_edit_text, contact_email_edit_text, contact_phone_edit_text;
    private Toolbar toolbar;
    private AutoCompleteTextView actvJobType,actvCurrency,actvLocationType;
    private Button btn_ilan_yayinla;
    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add_job, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        btn_ilan_yayinla = view.findViewById(R.id.btnSubmit);
        db = FirebaseFirestore.getInstance();

        job_title_edit_text = view.findViewById(R.id.etJobTitle);
        company_edit_text = view.findViewById(R.id.etCompany);
        location_edit_text = view.findViewById(R.id.etLocation);
        min_salary_edit_text = view.findViewById(R.id.etMinSalary);
        max_salary_edit_text = view.findViewById(R.id.etMaxSalary);
        description_edit_text = view.findViewById(R.id.etDescription);
        requirements_edit_text = view.findViewById(R.id.etRequirements);
        contact_email_edit_text = view.findViewById(R.id.etContactEmail);
        contact_phone_edit_text = view.findViewById(R.id.etContactPhone);

        if(activity != null){
            activity.setSupportActionBar(toolbar);
            //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Geri butonu (opsiyonel)
            activity.getSupportActionBar().setTitle("İş İlanı Oluştur");
        }
        // Çalışma Şekli için seçeneklerin gösterilmesi
        String[] job_types = {"Tam Zamanlı", "Yarı Zamanlı", "Günlük", "Freelance"};
        actvJobType = view.findViewById(R.id.actvJobType);

        ArrayAdapter<String> adapter_job_types = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,job_types);
        actvJobType.setAdapter(adapter_job_types);
        actvJobType.setOnClickListener(v -> actvJobType.showDropDown());

        // Work Location Type için seçeneklerin gösterilmesi
        String[] location_types = {"İş Yerinde","Hibrit","Uzaktan"};
        actvLocationType = view.findViewById(R.id.actvLocationType);
        ArrayAdapter<String> adapter_location_types = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,location_types);
        actvLocationType.setAdapter(adapter_location_types);
        actvLocationType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvJobType.showDropDown();
            }
        });

        // Para Birimi için seçeneklerin gösterilmesi
        String[] currency_types = {"₺","$","£","€"};
        actvCurrency = view.findViewById(R.id.actvCurrency);

        ArrayAdapter<String> adapter_currency = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line,currency_types);
        actvCurrency.setAdapter(adapter_currency);
        actvCurrency.setOnClickListener(v -> actvCurrency.showDropDown());

        // İlan yayınlamak için butona tıklandıgında gerçekleşicek işlem
        btn_ilan_yayinla.setOnClickListener(v -> ilan_yayinla());


        return view;
    }

    public void ilan_yayinla(){
        String jobTitle = job_title_edit_text.getText().toString().trim();
        String company = company_edit_text.getText().toString().trim();
        String location = location_edit_text.getText().toString().trim();
        String job_type = actvJobType.getText().toString().trim();
        String location_type = actvLocationType.getText().toString().trim();
        Integer min_salary = Integer.parseInt(min_salary_edit_text.getText().toString().trim());
        Integer max_salary = Integer.parseInt(max_salary_edit_text.getText().toString().trim());
        String currency = actvCurrency.getText().toString().trim();
        String description = description_edit_text.getText().toString().trim();
        String requirements = requirements_edit_text.getText().toString().trim();
        String contact_email = contact_email_edit_text.getText().toString().trim();
        String contact_phone = contact_phone_edit_text.getText().toString().trim();


        DocumentReference job_reference = db.collection("İlanlar").document();

        String jobID = job_reference.getId();


        // İlan verilerini map oluştur
        Map<String, Object> ilan_map = new HashMap<>();
        ilan_map.put("jobID",jobID);
        ilan_map.put("jobTitle", jobTitle);
        ilan_map.put("company", company);
        ilan_map.put("location", location);
        ilan_map.put("jobType", job_type);
        ilan_map.put("locationType",location_type);
        ilan_map.put("minSalary", min_salary);
        ilan_map.put("maxSalary", max_salary);
        ilan_map.put("currency", currency);
        ilan_map.put("description", description);
        ilan_map.put("requirements", requirements);
        ilan_map.put("contactEmail", contact_email);
        ilan_map.put("contactPhone", contact_phone);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            ilan_map.put("userId",user.getUid());
            ilan_map.put("user Email",user.getEmail());
        }

        job_reference.set(ilan_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(),"İlan başarıyla oluşturuldu.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Hata: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        /* Firestore'a kaydet
        db.collection("İlanlar").add(ilan_map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getContext(),"İlan Başarıyla Oluşturuldu!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Hata: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


         */

        job_title_edit_text.setText("");
        company_edit_text.setText("");
        location_edit_text.setText("");
        actvJobType.setText("");
        actvLocationType.setText("");
        min_salary_edit_text.setText("");
        max_salary_edit_text.setText("");
        actvCurrency.setText("");
        description_edit_text.setText("");
        requirements_edit_text.setText("");
        contact_email_edit_text.setText("");
        contact_phone_edit_text.setText("");


    }
}