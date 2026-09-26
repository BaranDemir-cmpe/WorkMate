package com.example.workmate;

import android.content.Context;
import android.net.Uri;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PdfTextExtractor {

    public static String extractTextFromPdf(Context context, Uri pdfUri){
        try{

            PDFBoxResourceLoader.init(context.getApplicationContext());
            File pdf = new File(pdfUri.getPath());
            InputStream inputStream = context.getContentResolver().openInputStream(pdfUri);
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            inputStream.close();
            return text;
        }catch (IOException e){
            e.printStackTrace();
            return "PDF okuma hatası: "+ e.getMessage();
        }catch (Exception e){
            e.printStackTrace();
            return "Genel hata: " + e.getMessage();
        }
    }
}
