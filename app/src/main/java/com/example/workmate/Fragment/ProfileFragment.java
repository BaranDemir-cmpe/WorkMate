package com.example.workmate.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workmate.Activity.IntroActivity;
import com.example.workmate.Activity.LoginActivity;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    private TextView textView_cikis_yap,textView_kaydedilen_ilanlar,textView_basvurulan_ilanlar,textView_olusturdugum_ilanlar,textView_profili_duzenle, textView_cv_yorumlama;
    private FirebaseAuth auth;
    private ImageView profile_picture_image;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        textView_cikis_yap = view.findViewById(R.id.txtViewCikisYap);
        textView_kaydedilen_ilanlar = view.findViewById(R.id.textView_kaydedilen_ilanlar);
        textView_basvurulan_ilanlar = view.findViewById(R.id.textView_basvurulan_ilanlar);
        textView_olusturdugum_ilanlar = view.findViewById(R.id.textView_olusturdugum_ilanlar);
        textView_profili_duzenle = view.findViewById(R.id.textView_profili_duzenle);
        profile_picture_image = view.findViewById(R.id.profile_picture_image);
        textView_cv_yorumlama = view.findViewById(R.id.textView_cv_yorumlama);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String userID = auth.getCurrentUser().getUid();


        textView_cikis_yap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();

                // Giris ekranına yönlendir
                Intent intent = new Intent(getActivity(), IntroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        textView_kaydedilen_ilanlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment SavedJobsFragment = new SavedJobsFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,SavedJobsFragment)
                        .addToBackStack(null).commit();
            }
        });

        textView_basvurulan_ilanlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment AppliedJobsFragment = new AppliedJobsFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,AppliedJobsFragment)
                        .addToBackStack(null).commit();
            }
        });

        textView_olusturdugum_ilanlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment MyCreatedJobsFragment = new MyCreatedJobsFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container,MyCreatedJobsFragment).addToBackStack(null).commit();
            }
        });

        textView_profili_duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment ProfileManagementFragment = new ProfileManagementFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,ProfileManagementFragment).addToBackStack(null).commit();
            }
        });

        textView_cv_yorumlama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment CvYorumlamaFragment = new CvYorumlamaFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,CvYorumlamaFragment).addToBackStack(null).commit();
            }
        });



        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    // Profil fotoğrafı gösterimi
                    String imageUrl = documentSnapshot.getString("profilResimleriUrl");
                    if(imageUrl != null && !imageUrl.isEmpty()){
                        Glide.with(getContext()).load(imageUrl).into(profile_picture_image);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Profil Resmini yüklerken hata oluştu.",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}