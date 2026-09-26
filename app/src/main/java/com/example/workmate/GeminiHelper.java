package com.example.workmate;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiHelper {

    private final GenerativeModel generativeModel;
    private final Executor executor = Executors.newSingleThreadExecutor();


    public interface GeminiCallback{
        void onResult(String output);
        void onError(String error);
    }

    public GeminiHelper(String apiKey){
        generativeModel = new GenerativeModel("gemini-1.5-flash",apiKey);
    }

    public void analyzeCV(String cvText, GeminiCallback callback){
        Content content = new Content.Builder().addText("Bu özgeçmiş hakkında geri bildirim ver. Güçlü ve zayıf yönlerini analiz et. Yapılabilecek iyileştirmeleri belirt. \n " + cvText).build();

        GenerativeModelFutures model = GenerativeModelFutures.from(generativeModel);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText != null && !resultText.isEmpty()) {
                    callback.onResult(resultText);
                } else {
                    callback.onError("Yapay zekadan boş yanıt geldi.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                callback.onError("Gemini API Hatası: " + t.getMessage());
            }
        }, executor);


        }
}

