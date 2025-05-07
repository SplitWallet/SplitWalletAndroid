package com.example.splitwallet.models;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private String title;
    private String message;

    public NotificationRequest(String title, String message)
    {
        this.title = title;
        this.message = message;
    }
}