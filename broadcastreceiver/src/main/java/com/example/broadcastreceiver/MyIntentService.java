package com.example.broadcastreceiver;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");
    }

    //백그라운드에서 수행할 내용 작성
    //게임같은 경우는 하나의 필드를 가져와서 게임을 진행하고 있는 경우
    //근처의 다른 필드를 다운로드
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        for(int i=0; i<10; i=i+1){
            SystemClock.sleep(1000);
            Log.e("TAG", "Intent Service :" + i);
        }
    }
}
