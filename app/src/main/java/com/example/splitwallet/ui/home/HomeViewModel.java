package com.example.splitwallet.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Чтобы начать работу, выберите, создайте или присоединитесь к группе в меню слева");
    }

    public LiveData<String> getText() {
        return mText;
    }
}