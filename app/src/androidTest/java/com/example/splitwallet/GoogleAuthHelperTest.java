//package com.example.splitwallet;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.tasks.Task;
//
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class GoogleAuthHelperTest {
//
//    @Rule
//    public ActivityScenarioRule<TestAuthActivity> activityScenarioRule =
//            new ActivityScenarioRule<>(TestAuthActivity.class);
//
//    @Test
//    public void testHandleSignInResult_successful() {
//        activityScenarioRule.getScenario().onActivity(activity -> {
//            // Мокаем колбэк
//            GoogleAuthHelper.GoogleAuthCallback mockCallback = mock(GoogleAuthHelper.GoogleAuthCallback.class);
//            GoogleAuthHelper authHelper = new GoogleAuthHelper(activity, mockCallback);
//
//            // Создаём мок аккаунта
//            GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
//            when(mockAccount.getIdToken()).thenReturn("mock_token");
//
//            // Подделываем Task
//            Task<GoogleSignInAccount> mockTask = Tasks.forResult(mockAccount);
//
//            // Подделываем Intent — GoogleSignIn.getSignedInAccountFromIntent вернёт mockTask
//            // Это проблемное место — требует PowerMock или замены GoogleSignIn обёрткой
//
//            // Временный хак: мы не вызываем getSignedInAccountFromIntent напрямую — тестируем через рефакторинг
//
//            // Вызов метода
//            authHelper.handleSignInResult(/* mocked or dummy Intent */ null);
//
//            // Проверка
//            verify(mockCallback).onSuccess("mock_token");
//            verify(mockCallback, never()).onError(anyString());
//        });
//    }
//}
//
