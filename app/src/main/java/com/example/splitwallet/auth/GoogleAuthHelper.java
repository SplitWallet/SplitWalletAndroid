package com.example.splitwallet.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.splitwallet.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleAuthHelper {
    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleAuthHelper";

    private final Activity activity;
    private final GoogleSignInClient googleSignInClient;
    private final GoogleAuthCallback callback;

    public interface GoogleAuthCallback {
        void onSuccess(String idToken);
        void onError(String error);
    }

    public GoogleAuthHelper(Activity activity, GoogleAuthCallback callback) {
        this.activity = activity;
        this.callback = callback;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                callback.onSuccess(account.getIdToken());
            } else {
                callback.onError("Failed to get ID token");
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            callback.onError("Google sign in failed: " + e.getStatusCode());
        }
    }
}