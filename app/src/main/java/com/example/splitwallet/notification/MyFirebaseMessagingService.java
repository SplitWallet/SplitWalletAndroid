package com.example.splitwallet.notification;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.splitwallet.R;
import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.ui.GroupPagerActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Обработка входящих сообщений
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("token", null);

        if (authToken != null) {
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            // Используем новый метод updateFcmToken
            Call<Void> call = apiService.updateFcmToken("Bearer " + authToken, token);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e("FCM", "Failed to send token to server. Code: " + response.code());
                    } else {
                        Log.d("FCM", "Token successfully sent to server");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("FCM", "Failed to send token to server", t);
                }
            });
        } else {
            Log.e("FCM", "Auth token is null, can't send FCM token to server");
        }
    }
}