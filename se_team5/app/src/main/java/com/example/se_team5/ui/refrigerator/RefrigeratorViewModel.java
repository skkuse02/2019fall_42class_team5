package com.example.se_team5.ui.refrigerator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RefrigeratorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RefrigeratorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is refrigerator fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}