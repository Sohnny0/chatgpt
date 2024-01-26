package com.plexpt.chatgpt;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.alibaba.fastjson.JSON;
import com.plexpt.chatgpt.api.Api;
import com.plexpt.chatgpt.entity.BaseResponse;
import com.plexpt.chatgpt.entity.images.Edits;
import com.plexpt.chatgpt.entity.images.Generations;
import com.plexpt.chatgpt.entity.images.ImagesRensponse;
import com.plexpt.chatgpt.entity.images.Variations;
import com.plexpt.chatgpt.exception.ChatException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author matoooo
 * @Date 2023/8/25 11:08
 * @Description: 调用图片相关接口
 */
@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Images {
    /**
     * keys
     */
    private String apiKey;

    private List<String> apiKeyList;
    /**
     * 自定义api host使用builder的方式构造client
     */
    @Builder.Default
    private String apiHost = Api.DEFAULT_API_HOST;
    private Api apiClient;
    private OkHttpClient okHttpClient;
    /**
     * 超时 默认300
     */
    @Builder.Default
    private long timeout = 300;
    /**
     * okhttp 代理
     */
    @Builder.Default
    private Proxy proxy = Proxy.NO_PROXY;

    /**
     * 初始化
     */
    public Images init() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(chain -> {
            Request original = chain.request();
            String key = apiKey;
            if (apiKeyList != null && !apiKeyList.isEmpty()) {
                key = RandomUtil.randomEle(apiKeyList);
            }

            Request request = original.newBuilder()
                    .header(Header.AUTHORIZATION.getValue(), "Bearer " + key)
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }).addInterceptor(chain -> {
            Request original = chain.request();
            Response response = chain.proceed(original);
            if (!response.isSuccessful()) {
                String errorMsg = response.body().string();

                log.error("请求异常：{}", errorMsg);
                BaseResponse baseResponse = JSON.parseObject(errorMsg, BaseResponse.class);
                if (Objects.nonNull(baseResponse.getError())) {
                    log.error(baseResponse.getError().getMessage());
                    throw new ChatException(baseResponse.getError().getMessage());
                }
                throw new ChatException("error");
            }
            return response;
        });

        client.connectTimeout(timeout, TimeUnit.SECONDS);
        client.writeTimeout(timeout, TimeUnit.SECONDS);
        client.readTimeout(timeout, TimeUnit.SECONDS);
        if (Objects.nonNull(proxy)) {
            client.proxy(proxy);
        }
        OkHttpClient httpClient = client.build();
        this.okHttpClient = httpClient;


        this.apiClient = new Api(okHttpClient, this.apiHost);

        return this;
    }

    public ImagesRensponse generations(Generations generations) throws IOException {
        return this.apiClient.imageGenerations(generations);
    }

    public ImagesRensponse edits(File image,File mask,Edits edits){
        RequestBody i = RequestBody.create(MediaType.parse("multipart/form-data;charset=UTF-8"), image);
        MultipartBody.Part iPart = MultipartBody.Part.createFormData("image", image.getName(), i);

        RequestBody m = RequestBody.create(MediaType.parse("multipart/form-data;charset=UTF-8"), mask);
        MultipartBody.Part mPart = MultipartBody.Part.createFormData("mask", mask.getName(), m);

        try {
            return this.apiClient.imageEdits(iPart,mPart,edits);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImagesRensponse variations(File image,Variations variations){
        RequestBody i = RequestBody.create(MediaType.parse("multipart/form-data;charset=UTF-8"), image);
        MultipartBody.Part iPart = MultipartBody.Part.createFormData("image", image.getName(), i);

        try {
            return this.apiClient.imageVariations(iPart,variations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
