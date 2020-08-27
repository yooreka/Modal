package com.example.broadcastreceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.broadcastreceiver.R;

public class PlayService extends Service
        implements MediaPlayer.OnCompletionListener{

    //음원 재생 가능한 클래스의 참조형 변수
    MediaPlayer player;

    //서비스와의 데이터 공유에 사용할 Broadcast Receiver
    BroadcastReceiver receiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(
                        Context context, Intent intent) {
                    //전송해 준 데이터 읽기
                    //stop 이나 start 라는 문자열을 전송 - mode
                    String mode =
                            intent.getStringExtra("mode");
                    if(mode != null){
                        if(mode.equals("start")){
                            try{
                                //재생 중이라면
                                if(player != null && player.isPlaying()){
                                    //재생을 중지하고 메모리 정리
                                    player.stop();
                                    //메모리 해제
                                    player.release();
                                    //가베지 컬렉터를 호출할 수 있도록 해주는 구문
                                    player = null;
                                }
                                //새로 생성
                                player = MediaPlayer.create(
                                        getApplicationContext(), R.raw.test);
                                player.start();
                                //리시버를 호출
                                Intent aIntent = new Intent(
                                        "com.example.PLAY_TO_ACTIVITY");
                                //음원이 재생 중인지 확인해 줄 수 있도록 해주기 위한 값
                                aIntent.putExtra("mode", "start");
                                //재생 중인 위치를 알 수 있도록 해주기 위한 값
                                aIntent.putExtra("duration", player.getDuration());
                                sendBroadcast(aIntent);
                            }catch(Exception e){
                                Log.e("음원 재생 예외", e.getMessage());
                                e.printStackTrace();
                            }
                        }else if(mode.equals("stop")){
                            if(player!= null && player.isPlaying()){
                                player.stop();
                            }
                            player.release();
                            player = null;
                        }
                    }
                }
            };

    //MediaPlayer.OnCompletionListener 인터페이스의
    //재생이 종료되었을 때 호출되는 메소드
    @Override
    public void onCompletion(MediaPlayer mp){
        //종료 되었으므로 종료 되었다고 방송을 하고 서비스를 중지
        Intent intent = new Intent(
                "com.example.PLAY_TO_ACTIVITY");
        intent.putExtra("mode", "stop");
        sendBroadcast(intent);
        //서비스를 중지 - 이 메소드를 호출하지 않으면 서비스는 계속 살아있음
        stopSelf();
    }

    //서비스가 만들어질 때 호출되는 메소드
    @Override
    public void onCreate(){
        super.onCreate();
        //리시버 등록
        registerReceiver(receiver,
                new IntentFilter("com.example.PLAY_TO_SERVICE"));
    }

    //서비스가 종료될 때 호출되는 메소드
    @Override
    public void onDestroy(){
        //리시버 등록 해제
        unregisterReceiver(receiver);
        //파괴할 때는 상위 클래스의 메소드를 뒤에서 호출 - 소멸자
        super.onDestroy();
    }

    //서비스가 중지되었다가 재시작 되었을 때 호출되는 메소드
    @Override
    public int onStartCommand(
            Intent intent, int flags, int startId){
        if(player != null){
            Intent aIntent = new Intent(
                    "com.example.PLAY_TO_ACTIVITY");
            aIntent.putExtra("mode", "restart");
            aIntent.putExtra("duration",
                    player.getDuration());
            aIntent.putExtra("current",
                    player.getCurrentPosition());
            sendBroadcast(aIntent);
        }
        //리턴이 있는 메소드를 오버라이딩 할 때 메소드의 역할을 잘 모르겠으면
        //상위 클래스의 메소드를 호출해서 리턴하면 됩니다.
        return super.onStartCommand(intent, flags, startId);
    }

    public PlayService() {
    }

    @Override
    //스타트 서비스 일 때는 필요가 없고 바운드 서비스에서만
    //구현
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
