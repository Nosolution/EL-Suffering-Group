package com.example.el_project;

/*
*
* 重写应用腾讯的一个接口
* 为分享这是必须的
*
* */

import android.util.Log;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class MyIUiListener implements IUiListener {
    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {
        Log.d("SHARE", "onError: Share Error");
    }

    @Override
    public void onCancel() {

    }
}
