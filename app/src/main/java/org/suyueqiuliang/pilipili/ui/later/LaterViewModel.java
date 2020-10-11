package org.suyueqiuliang.pilipili.ui.later;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LaterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LaterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is later fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}