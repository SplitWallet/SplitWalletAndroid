package com.example.splitwallet.notification;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.splitwallet.R;
import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.NotificationRequest;
import com.example.splitwallet.models.TokenRequest;
import com.example.splitwallet.ui.GroupDetailsActivity;
import com.example.splitwallet.ui.GroupPagerActivity;
import com.example.splitwallet.ui.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    String newToken = "";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Refreshed token: " + token);
        this.newToken = token;
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Получено сообщение от: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Тело уведомления: " + remoteMessage.getNotification().getBody());
            // Здесь можно показать уведомление пользователю
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Создаем уведомление
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "expenses_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Добавляем действие при нажатии (открытие группы)
            if (remoteMessage.getData().containsKey("groupId")) {
                Intent intent = new Intent(this, GroupPagerActivity.class);
                intent.putExtra("GROUP_ID", Long.parseLong(remoteMessage.getData().get("groupId")));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                builder.setContentIntent(pendingIntent);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            NotificationManagerCompat.from(this).notify(1, builder.build());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Данные сообщения: " + remoteMessage.getData());
            // Обработка данных
        }
        // Обработка входящих сообщений
    }

    public void sendNotification(String token, String userId, String title, String body) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        NotificationRequest request = new NotificationRequest("hello", "bois");
        request.setTitle(title);
        //request.setBody(body);

        apiService.sendNotification("Bearer " + token, userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // обработка ответа
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // обработка ошибки
            }
        });
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String authToken = sharedPreferences.getString("token", null);

        if (authToken != null) {
            String userId = extractUserIdFromJwt(authToken);

            if (userId != null) {
                ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                TokenRequest tokenRequest = new TokenRequest(token);

                Call<Void> call = apiService.updateFcmToken("Bearer " + authToken, userId, tokenRequest);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Log.e("FCM", "Failed to send token to server. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("FCM", "Failed to send token to server", t);
                    }
                });
            }
        }

    }
    private String extractUserIdFromJwt(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length >= 2) {
                String payloadJson = new String(
                        android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
                org.json.JSONObject payload = new org.json.JSONObject(payloadJson);
                return payload.getString("sub"); // или другой ключ, где хранится userId
            }
        } catch (Exception e) {
            Log.e("JWT", "Error extracting userId from token", e);
        }
        return null;
    }


}