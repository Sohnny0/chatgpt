module com.plexpt.chatgpt {
    requires static lombok;
    requires okhttp3;
    requires cn.hutool;
    requires fastjson;
    requires com.fasterxml.jackson.databind;
    requires okhttp3.sse;
    requires spring.webmvc;
    requires jtokkit;
    requires org.slf4j;
    requires feign.okhttp;
    requires feign.core;

    exports com.plexpt.chatgpt;
    exports com.plexpt.chatgpt.api;
    exports com.plexpt.chatgpt.entity;
    exports com.plexpt.chatgpt.entity.chat;
    exports com.plexpt.chatgpt.entity.billing;
    exports com.plexpt.chatgpt.entity.audio;
    exports com.plexpt.chatgpt.entity.audio.enums;
    exports com.plexpt.chatgpt.entity.images;
    exports com.plexpt.chatgpt.entity.images.enums;
    exports com.plexpt.chatgpt.exception;
    exports com.plexpt.chatgpt.listener;
    exports com.plexpt.chatgpt.util;

}