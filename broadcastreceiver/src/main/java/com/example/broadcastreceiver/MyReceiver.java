package com.example.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       //토스트 하나 출력
        Toast.makeText(context, "방송을 수신했습니다.", Toast.LENGTH_LONG).show();
        //MainActivity를 출력
        //다른 Application에서 이 Application을 실행
        Intent mainIntent = new Intent(context, broadcastActivity.class);
        //실행을 하지 않았더라도 설치만 되어 있으면 호출될 수 잇도록 설정
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainIntent);
    }
}
