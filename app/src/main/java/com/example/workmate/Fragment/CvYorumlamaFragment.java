package com.example.workmate.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workmate.GeminiHelper;
import com.example.workmate.PdfTextExtractor;
import com.example.workmate.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.example.workmate.BuildConfig;

public class CvYorumlamaFragment extends Fragment {


    private Button btn_pick_pdf, btn_analyze_pdf;
    private TextView textView_result;
    private Uri selectedPdfUri;
    private GeminiHelper geminiHelper;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    private CircularProgressIndicator progress_bar;

    public CvYorumlamaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cv_yorumlama, container, false);

        btn_pick_pdf = view.findViewById(R.id.btn_pick_pdf);
        btn_analyze_pdf = view.findViewById(R.id.btn_analyze_pdf);
        textView_result = view.findViewById(R.id.text_result);
        progress_bar = view.findViewById(R.id.progress_bar);


        pdfPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
            if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                selectedPdfUri = result.getData().getData();
                Toast.makeText(getContext(),"CV Seçildi.",Toast.LENGTH_SHORT).show();
            }
        });

        geminiHelper = new GeminiHelper(BuildConfig.GEMINI_API_KEY);

        btn_pick_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                pdfPickerLauncher.launch(intent);
            }
        });

        btn_analyze_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPdfUri == null){
                    Toast.makeText(getContext(),"Lütfen önce bir CV dosyası seçin.",Toast.LENGTH_SHORT).show();
                    return;
                }

                progress_bar.setVisibility(View.VISIBLE);
                textView_result.setText("Analiz Ediliyor");

                String cvText = PdfTextExtractor.extractTextFromPdf(getContext(),selectedPdfUri);

                geminiHelper.analyzeCV(cvText, new GeminiHelper.GeminiCallback() {
                    @Override
                    public void onResult(String output) {
                        requireActivity().runOnUiThread(()->{
                            progress_bar.setVisibility(View.GONE);
                            textView_result.setText(output);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        requireActivity().runOnUiThread(()->{
                            progress_bar.setVisibility(View.GONE);
                            textView_result.setText("Hata: "+ error);
                        });
                    }
                });
            }
        });

        return view;
    }
}