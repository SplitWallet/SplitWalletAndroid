package com.example.splitwallet.models;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private String title;
    private String body;
    private Map<String, String> data;
}