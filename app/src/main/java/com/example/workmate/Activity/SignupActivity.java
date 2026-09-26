package com.example.workmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workmate.R;
import com.example.workmate.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText ad_edit_text, soyad_edit_text, email_edit_text, password_edit_text, repassword_edit_text;
    private Button signup_btn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ad_edit_text = findViewById(R.id.editTextAd);
        soyad_edit_text = findViewById(R.id.editTextSoyad);
        email_edit_text = findViewById(R.id.editTextEmail);
        password_edit_text = findViewById(R.id.editTextPassword);
        repassword_edit_text = findViewById(R.id.editTextRepassword);
        signup_btn = findViewById(R.id.signupButon);

        auth = FirebaseAuth.getInstance();

        signup_btn.setOnClickListener(v -> registeruser());

    }

    private void registeruser(){

        String ad = ad_edit_text.getText().toString().trim().toLowerCase();
        String soyad = soyad_edit_text.getText().toString().trim().toLowerCase();
        String email = email_edit_text.getText().toString().trim();
        String password = password_edit_text.getText().toString().trim();
        String repassword = repassword_edit_text.getText().toString().trim();

        // Basit doğrulama
        if(ad.isEmpty() || soyad.isEmpty() || email.isEmpty() || password.isEmpty() || repassword.isEmpty()){
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(repassword)){
            Toast.makeText(this, "Parolalar uyuşmuyor!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6){
            Toast.makeText(this, "Parola en az 6 karakter olmalıdır!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Firebase auth ile kayıt işlemi
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //FirebaseAuth User ID' yi alıyoruz

                    String uid = auth.getCurrentUser().getUid();

                    //Firestore instance
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Kullanıcı bilgilerini hashmap olarak sakla
                    Map<String,Object> user_map = new HashMap<>();
                    user_map.put("ad",ad);
                    user_map.put("soyad",soyad);
                    user_map.put("email",email);

                    // Firestore'da 'users' koleksiyonu içine uid ile kayıt
                    db.collection("users").document(uid).set(user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SignupActivity.this, "Kayıt ve kullanıcı bilgileri kaydedildi!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            intent.putExtra("fragmentToLoad","home");
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "Firestore Hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }else{
                    Toast.makeText(SignupActivity.this,"Hata: " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




}