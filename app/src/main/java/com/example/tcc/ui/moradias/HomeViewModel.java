package com.example.tcc.ui.moradias;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private TextView textView;

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
//        textView = findViewById(R.id.text_home);
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}