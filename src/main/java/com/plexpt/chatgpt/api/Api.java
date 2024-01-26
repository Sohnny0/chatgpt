package com.plexpt.chatgpt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.entity.audio.AudioResponse;
import com.plexpt.chatgpt.entity.billing.CreditGrantsResponse;
import com.plexpt.chatgpt.entity.billing.SubscriptionData;
import com.plexpt.chatgpt.entity.billing.UseageResponse;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.images.Generations;
import com.plexpt.chatgpt.entity.images.ImagesRensponse;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class Api {

    public static String DEFAULT_API_HOST = "https://api.openai.com/";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public Api(OkHttpClient client, String apiHost) {
        this.client = client;
        if (apiHost.endsWith("/")) {
            DEFAULT_API_HOST = apiHost;
        } else {
            DEFAULT_API_HOST = apiHost +"/";
        }
        this.objectMapper = new ObjectMapper();
    }

    /**
     * chat
     */
    public ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(chatCompletion));
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/chat/completions")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), ChatCompletionResponse.class);
        }
    }

    /**
     * image_generations
     */
    public ImagesRensponse imageGenerations(Generations generations) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(generations));
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/images/generations")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), ImagesRensponse.class);
        }
    }

    /**
     * image_edits
     */
    public ImagesRensponse imageEdits(MultipartBody.Part image, MultipartBody.Part mask, Map<String, RequestBody> edits) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addPart(image);
        builder.addPart(mask);
        for (Map.Entry<String, RequestBody> entry : edits.entrySet()) {
            builder.addFormDataPart(entry.getKey(), "", entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/images/edits")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), ImagesRensponse.class);
        }
    }

    /**
     * image_variations
     */
    public ImagesRensponse imageVariations(MultipartBody.Part image, Map<String, RequestBody> variations) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addPart(image);
        for (Map.Entry<String, RequestBody> entry : variations.entrySet()) {
            builder.addFormDataPart(entry.getKey(), "", entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/images/variations")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), ImagesRensponse.class);
        }
    }

    /**
     * audio_transcriptions
     */
    public AudioResponse audioTranscriptions(MultipartBody.Part audio, Map<String, RequestBody> transcriptions) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addPart(audio);
        for (Map.Entry<String, RequestBody> entry : transcriptions.entrySet()) {
            builder.addFormDataPart(entry.getKey(), "", entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/audio/transcriptions")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), AudioResponse.class);
        }
    }

    /**
     * audio_translations
     */
    public AudioResponse audioTranslations(MultipartBody.Part audio, Map<String, RequestBody> transcriptions) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addPart(audio);
        for (Map.Entry<String, RequestBody> entry : transcriptions.entrySet()) {
            builder.addFormDataPart(entry.getKey(), "", entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/audio/translations")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), AudioResponse.class);
        }
    }
    
    /**
     * 余额查询
     */
    public CreditGrantsResponse creditGrants() throws IOException {
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "dashboard/billing/credit_grants")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), CreditGrantsResponse.class);
        }
    }

    /**
     * 余额查询
     */
    public SubscriptionData subscription() throws IOException {
        Request request = new Request.Builder()
                .url(DEFAULT_API_HOST + "v1/dashboard/billing/subscription")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), SubscriptionData.class);
        }
    }

    /**
     * 余额查询
     */
    public UseageResponse usage(String startDate, String endDate) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(DEFAULT_API_HOST + "v1/dashboard/billing/usage").newBuilder();
        urlBuilder.addQueryParameter("start_date", startDate);
        urlBuilder.addQueryParameter("end_date", endDate);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return objectMapper.readValue(response.body().string(), UseageResponse.class);
        }
    }
}

