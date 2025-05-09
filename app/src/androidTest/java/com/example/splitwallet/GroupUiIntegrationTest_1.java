package com.example.splitwallet;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.LocalDateTimeDeserializer;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.repository.GroupRepository;
import com.example.splitwallet.ui.JoinGroupActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class GroupUiIntegrationTest_1 {

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString());
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testFullCreateAndJoinGroupFlow() throws Exception {
        // 1. Ответ для создания группы
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{ \"id\": 1, \"name\": \"Group UI\", \"uniqueCode\": \"UI123\" }"));

        // 2. Ответ для присоединения
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // 3. Запуск основной активности (например, MainActivity, где отображается GroupsFragment)
        ActivityScenario.launch(MainActivity.class);

        // 4. Кликаем по кнопке "Создать группу"
        onView(withId(R.id.create_group_fab)).perform(click());

        // 5. Вводим название
        onView(withId(R.id.dialog_group_name_input)).perform(typeText("Group UI"), closeSoftKeyboard());

        // 6. Нажимаем "Сохранить"
        onView(withText("Создать")).perform(click());

        // 7. Проверяем, что отображён код приглашения
        onView(withText(containsString("Код приглашения: UI123"))).check(matches(isDisplayed()));

        // 8. Открываем окно "Присоединиться к группе"
        onView(withId(R.id.join_group_button)).perform(click());

        // 9. Вводим код приглашения
        onView(withId(R.id.join_code_input)).perform(typeText("UI123"), closeSoftKeyboard());

        // 10. Нажимаем "Присоединиться"
        onView(withId(R.id.dialog_join_button)).perform(click());

        // 11. Проверяем, что пользователь успешно присоединился
        onView(withText("Вы присоединились к группе!")).check(matches(isDisplayed()));
    }
}
