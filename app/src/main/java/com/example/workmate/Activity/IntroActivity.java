package com.example.workmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.workmate.R;
import com.example.workmate.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    private TextView startBtn;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ConstraintLayout main_layout = findViewById(R.id.main_layout);


        auth = FirebaseAuth.getInstance();


        startBtn = findViewById(R.id.startBtn);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });

        // hesaptan çıkış yapmak için
        //FirebaseAuth.getInstance().signOut();

    }

    // 🔥 Uygulama açıldığında kullanıcı giriş yaptıysa direkt MainActivity'e geç
    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            // Oturum zaten açık → Ana sayfaya yönlendir
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        }
    }
}