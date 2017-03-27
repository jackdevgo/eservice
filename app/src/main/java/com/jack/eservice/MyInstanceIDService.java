package com.jack.eservice;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by jason on 2017/3/22.
 */

public class MyInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM","Token:"+token);
        sendRegistrationToServe(token);
    }

    private void sendRegistrationToServe(String token) {

    }
}
