package com.example.workmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp_text_view,sifre_yenileme;
    private EditText email_edit_text, password_edit_text;
    private Button login_btn;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        signUp_text_view = findViewById(R.id.signUpBtn);
        email_edit_text = findViewById(R.id.editTextEmail);
        password_edit_text = findViewById(R.id.editTextPassword);
        login_btn = findViewById(R.id.loginBtn);
        sifre_yenileme = findViewById(R.id.sifreYenilemeTextView);



        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }


        signUp_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_edit_text.getText().toString().trim();
                String password = password_edit_text.getText().toString().trim();

                if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Lütfen mail ve şifre giriniz!",Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen mail giriniz!",Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen şifre giriniz!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Firebase giriş kontrolü
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("fragmentToLoad","home");
                            startActivity(intent);
                            finish();
                        }else{
                            // Hatalı  Giriş
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(LoginActivity.this, "Bu e-posta ile kayıtlı kullanıcı yok.", Toast.LENGTH_LONG).show();
                                email_edit_text.setText("");
                            }catch(FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(LoginActivity.this, "Şifre yanlış!", Toast.LENGTH_LONG).show();
                                password_edit_text.setText("");
                            }catch(Exception e){
                                Toast.makeText(LoginActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

            }
        });

        sifre_yenileme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_edit_text.getText().toString().trim();
                if(email.isEmpty()){
                    //Toast.makeText(LoginActivity.this, "Lütfen e-posta adresinizi girin!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content),"Lütfen e-posta adresini girin!",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(LoginActivity.this, "Şifre yenileme bağlantısı e-posta adresinize gönderildi",Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(android.R.id.content),"Şifre yenileme bağlantısı e-posta adresinize gönderildi!",Snackbar.LENGTH_SHORT).show();
                        }else{
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                // E-posta kayıtlı degil
                                Snackbar.make(findViewById(android.R.id.content),"Bu e-posta adresi kayıtlı değil!",Snackbar.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Snackbar.make(findViewById(android.R.id.content),"Hata: " + e.getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

    }
}