package com.example.nested.ui.main;

import android.net.Uri;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.squareup.picasso.RequestCreator;

public class PageViewModel extends ViewModel {

    private MutableLiveData<String> mUid = new MutableLiveData<>();
    private MutableLiveData<String> mName = new MutableLiveData<>();
    private MutableLiveData<Uri> mFirstUri = new MutableLiveData<>();
    private MutableLiveData<RequestCreator> mRC = new MutableLiveData<>();
    /*private LiveData<String> mText = Transformations.map(mUid, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "Hello world from section: " + input;
        }
    });*/

    public void setUid(String uid) {
        mUid.setValue(uid);
    }

    public LiveData<String> getUid() {
        return mUid;
    }

    public void setName(String name) {
        mName.setValue(name);
    }

    public LiveData<String> getName() {
        return mName;
    }

    public void setFirstUri(Uri uri) {
        mFirstUri.setValue(uri);
    }

    public LiveData<Uri> getFirstUri() {
        return mFirstUri;
    }

    public void setRC(RequestCreator rc) {
        mRC.setValue(rc);
    }

    public LiveData<RequestCreator> getRC() {
        return mRC;
    }
}