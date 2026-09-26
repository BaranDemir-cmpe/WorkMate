package com.example.workmate.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileManagementFragment extends Fragment {

    public ProfileManagementFragment(){

    }
    private static final int REQUEST_CODE_IMAGE_PICK = 1001;
    private static final int REQUEST_CODE_PDF_PICK = 1002;

    private EditText editTextName, editTextSurname, editTextLocation, editTextBirthdate, editTextProfession, editTextPhoneNumber;
    private AutoCompleteTextView autoCompleteTextViewEducationStatus, autoCompleteTextViewGender;
    private Button buttonSaveProfileManagement, buttonUploadCv;
    private ImageView profileManagementImage;
    private String selectedEducationStatus, selectedGender;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Uri selectedImageUri, selectedPdfUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_management, container, false);

        editTextName = view.findViewById(R.id.editText_Name);
        editTextSurname = view.findViewById(R.id.editText_Surname);
        editTextLocation = view.findViewById(R.id.editText_Location);
        editTextBirthdate = view.findViewById(R.id.editText_birthDate);
        editTextProfession = view.findViewById(R.id.editText_Profession);
        editTextPhoneNumber = view.findViewById(R.id.editText_phoneNumber);
        buttonSaveProfileManagement = view.findViewById(R.id.profileManagementSaveButton);
        profileManagementImage = view.findViewById(R.id.profileManagementImage);
        buttonUploadCv = view.findViewById(R.id.profileManagement_upload_cv_button);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();

        // EducationStatus için seçeneklerin gösterilmesi
        String[] educationStatusTypes = {"Seçiniz", "İlkokul Mezunu", "Ortaokul Mezunu", "Lise Mezunu" ,"Lisans Öğrencisi",
                "Ön Lisans Mezunu", "Lisans Mezunu", "Yüksek Lisans Mezunu", "Doktora Mezunu"};

        autoCompleteTextViewEducationStatus = view.findViewById(R.id.actvEducationStatus);

        // ArrayAdapter Oluşturma
        ArrayAdapter<String> adapterEducationStatus = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,educationStatusTypes);

        //Adapterı AutoCompleteTextView a bağlama
        autoCompleteTextViewEducationStatus.setAdapter(adapterEducationStatus);
        // Seçim yapıldığında yakala
        autoCompleteTextViewEducationStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEducationStatus = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(),"Seçilen: " + selectedEducationStatus,Toast.LENGTH_SHORT).show();
            }
        });

        // Cinsiyet için seçeneklerin gösterilmesi
        String[] genderTypes = {"Erkek","Kadın"};
        autoCompleteTextViewGender = view.findViewById(R.id.actv_Gender);

        // ArrayAdapter olusturma
        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,genderTypes);

        // Adapter ı AutoCompleteTextView bağlama
        autoCompleteTextViewGender.setAdapter(adapterGender);

        // Seçim yapıldığında yakala
        autoCompleteTextViewGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(),"Seçilen: " + selectedGender,Toast.LENGTH_SHORT).show();
            }
        });

        buttonSaveProfileManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String surname = editTextSurname.getText().toString().trim();
                String location = editTextLocation.getText().toString().trim();
                String birthDate = editTextBirthdate.getText().toString().trim();
                String profession = editTextProfession.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();

                // Map olarak veriyi hazırla
                Map<String,Object> userMap = new HashMap<>();
                userMap.put("name",name);
                userMap.put("surname",surname);
                userMap.put("location",location);
                userMap.put("birthDate",birthDate);
                userMap.put("profession",profession);
                userMap.put("phoneNumber",phoneNumber);
                userMap.put("educationStatus",selectedEducationStatus);
                userMap.put("gender",selectedGender);

                db.collection("users").document(userID).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"Profil kaydedildi!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Hata: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

                if(selectedImageUri != null){
                    uploadImageToFirebase(selectedImageUri,userID);
                }


            }
        });

        // Resim seçici (modern activity result API)
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult()
                ,result -> {
                    if(result.getResultCode()== Activity.RESULT_OK && result.getData() != null){
                        selectedImageUri = result.getData().getData();
                        profileManagementImage.setImageURI(selectedImageUri); // ImageView da göster
                    }
        });

        profileManagementImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });

        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    editTextName.setText(documentSnapshot.getString("name"));
                    editTextSurname.setText(documentSnapshot.getString("surname"));
                    editTextLocation.setText(documentSnapshot.getString("location"));
                    editTextBirthdate.setText(documentSnapshot.getString("birthDate"));
                    editTextProfession.setText(documentSnapshot.getString("profession"));
                    editTextPhoneNumber.setText(documentSnapshot.getString("phoneNumber"));

                    // AutoCompleteTextView'lara set et
                    String educationStatus = documentSnapshot.getString("educationStatus");
                    if(educationStatus != null){
                        autoCompleteTextViewEducationStatus.setText(educationStatus,false);
                        for(int i=0; i < adapterEducationStatus.getCount();i++){
                            if(adapterEducationStatus.getItem(i).equalsIgnoreCase(educationStatus)){
                                autoCompleteTextViewEducationStatus.setText(adapterEducationStatus.getItem(i),false);
                                selectedEducationStatus = adapterEducationStatus.getItem(i);
                                break;
                            }
                        }
                    }

                    String gender = documentSnapshot.getString("gender");
                    if(gender != null){
                        autoCompleteTextViewGender.setText(gender,false);
                        // Adapter'da varsa tekrar tetikle (eşleşmeyi garantiye alır)
                        for (int i = 0; i < adapterGender.getCount(); i++) {
                            if (adapterGender.getItem(i).equalsIgnoreCase(gender)) {
                                autoCompleteTextViewGender.setText(adapterGender.getItem(i), false);
                                selectedGender = adapterGender.getItem(i); // güvenli
                                break;
                            }
                        }
                    }

                    // Profil fotoğrafı gösterimi
                    String imageUrl = documentSnapshot.getString("profilResimleriUrl");
                    if(imageUrl != null && !imageUrl.isEmpty()){
                        Glide.with(requireContext()).load(imageUrl).into(profileManagementImage);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Hata: " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        ActivityResultLauncher<Intent> pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
           if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
               selectedPdfUri = result.getData().getData();
               uploadPdfTpFirebase(selectedPdfUri,userID);
           }
        });

        buttonUploadCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                pdfPickerLauncher.launch(intent);
            }
        });




        return view;
    }

    private void uploadImageToFirebase(Uri selectedImageUri, String userID){
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("profil_resimleri/" + userID + ".jpg");

        imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();

            FirebaseFirestore.getInstance().collection("users").document(userID).update("profilResimleriUrl",imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Profil ve görsel kaydedildi!", Toast.LENGTH_SHORT).show();
                }
            });
        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Resim yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadPdfTpFirebase(Uri PdfUri, String userID){
        StorageReference pdfReference = FirebaseStorage.getInstance().getReference("cv_dosyalar/" + userID + ".pdf");

        pdfReference.putFile(PdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pdfReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String pdfUrl = uri.toString();

                        FirebaseFirestore.getInstance().collection("users").document(userID).update("cvUrl", pdfUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"CV Yüklendi", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Firestore güncellenemedi: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"PDF yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

